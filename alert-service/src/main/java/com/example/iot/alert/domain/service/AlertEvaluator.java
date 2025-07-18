package com.example.iot.alert.domain.service;

import com.example.iot.alert.domain.dto.AlertLevel;
import com.example.iot.alert.domain.dto.AlertLogDto;
import com.example.iot.alert.domain.mapper.AlertLogMapper;
import com.example.iot.alert.domain.model.AlertLogEntity;
import com.example.iot.alert.domain.model.AlertRuleEntity;
import com.example.iot.alert.domain.repo.AlertLogRepository;
import com.example.iot.alert.domain.repo.AlertRuleRepository;
import com.example.iot.alert.domain.dto.SensorRecord;
import com.example.iot.alert.events.AlertPublisher;
import com.example.iot.alert.messaging.notifier.EmailNotifier;
import com.example.iot.alert.messaging.notifier.TelegramNotifier;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Evaluates each metric coming from the ingestion in real-time,
 * based on the rules in {@code alert_rule}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlertEvaluator {

    private final AlertRuleRepository ruleRepo;
    private final AlertLogRepository logRepo;
    private final AlertLogMapper mapper;
    private final AlertPublisher publisher;  // component that exposes alerts in UI / webhook etc.
    private final TelegramNotifier notifier;
    private final EmailNotifier emailNotifier;

    /** <sensorId|field, List<AlertRuleEntity>> – cache updated periodically. */
    private final Map<String, List<AlertRuleEntity>> ruleCache = new ConcurrentHashMap<>();

    /** (sensorId + "|" + field)  ➜  last observed value */
    private final Map<String, Double> lastValueCache = new ConcurrentHashMap<>();

    // ANSI escape codes for colored console output
    private static final String ANSI_RED = "\u001B";

    /* ------------------------------------------------------------------ */
    /* Cache lifecycle                                                    */
    /* ------------------------------------------------------------------ */

    /** The first populated immediately after startup. */
    @PostConstruct
    public void warmUp() {
        refreshRuleCache();
    }

    /** Refresh every 60 s – enough for runtime-edits in the UI. */
    @Scheduled(fixedRate = 60_000)
    public void refreshRuleCache() {
        Map<String, List<AlertRuleEntity>> newCache = new HashMap<>();
        ruleRepo.findByEnabledTrueOrderBySensorIdAscFieldAsc()
                .forEach(rule -> {
                    String key = cacheKey(rule.getSensorId(), rule.getField());
                    newCache.computeIfAbsent(key, k -> new ArrayList<>()).add(rule);
                });
        ruleCache.clear();
        ruleCache.putAll(newCache);
        log.debug("Rule cache refreshed – {} keys / {} rules",
                ruleCache.size(), newCache.values().stream().mapToInt(List::size).sum());
    }

    /* ------------------------------------------------------------------------ */
    /* 1) Severity hierarchy (you can adjust the map or use an Enum.ordinal())  */
    /* ------------------------------------------------------------------------ */
    private static final Map<AlertLevel, Integer> SEVERITY = Map.of(
            AlertLevel.CRITICAL, 1,
            AlertLevel.WARN,  2,
            AlertLevel.INFO,     3
    );

    /* ------------------------------------------------------------------------ */
    /* 2) Comparator that sets the strictest rules first;
    /* at equal level, we keep the most restrictive threshold                   */
    /* ------------------------------------------------------------------------ */
    private static final Comparator<AlertRuleEntity> MOST_SEVERE_FIRST =
            Comparator.comparingInt((AlertRuleEntity r) -> SEVERITY.get(r.getLevel()))
                    .reversed()                             // CRITICAL > WARNING > INFO
                    .thenComparing(AlertRuleEntity::getThreshold,
                            Comparator.reverseOrder());

    /* ------------------------------------------------------------------ */
    /* Public API – is called from MQTT listener / kafka consumer         */
    /* ------------------------------------------------------------------ */

    public void evaluate(SensorRecord rec) {
        List<AlertRuleEntity> rulesForSensor =
                ruleCache.getOrDefault(cacheKey(rec.sensorId(), rec.field()), List.of());

        if (rulesForSensor.isEmpty()) {
            return;
        }

        rulesForSensor.stream()
                .filter(rule -> matches(rule, rec.value()))
                .max(MOST_SEVERE_FIRST)
                .ifPresent(rule -> fireAlert(rule, rec));
    }

    /* ------------------------------------------------------------------ */
    /* Private Helpers                                                    */
    /* ------------------------------------------------------------------ */

    /** Create AlertLogEntity + persist + publish. */
    private void fireAlert(AlertRuleEntity rule, SensorRecord rec) {

        AlertLogEntity entity = AlertLogEntity.builder()
                .created(Instant.now())
                .sensorId(rec.sensorId())
                .field(rec.field())
                .ruleCode(rule.getId().toString())
                .level(rule.getLevel())           // you can decide level depending on op/threshold
                .message(buildMessage(rule, rec.value()))
                .ack(Boolean.FALSE)
                .build();

        AlertLogEntity saved = logRepo.save(entity);
        log.info("⚠️  Alert fired: {}", saved.getMessage());

        // MapStruct -> DTO -> websocket/email etc.
        AlertLogDto dto = mapper.toDto(saved);
        publisher.publish(dto);
        // use emoji icon for severity level
        String icon = switch (rule.getLevel()) {
            case CRITICAL -> ANSI_RED + "⛔" + ANSI_RED;
            case WARN     -> "⚠️";
            case INFO     -> "ℹ️";
        };
        notifier.send(icon + " " + buildMessage(rule, rec.value()));
        String subject = "⚠️  Alert: " + rule.getSensorId() + " - " + rule.getField();
        emailNotifier.send(subject, buildMessage(rule, rec.value()));
    }

    private static String buildMessage(AlertRuleEntity rule, double value) {
        String operator;
        switch (rule.getOp()){
            case LT  -> operator = "<";
            case LTE -> operator = "<=";
            case EQ  -> operator = "==";
            case GTE -> operator = ">=";
            case GT  -> operator = ">";
            case CHANGE -> operator = "changed";
            default -> throw new IllegalArgumentException("Unknown operation: " + rule.getOp());
        }
        return "Rule triggered: "
             + rule.getSensorId() + "." + rule.getField() + " "
             + operator + " " + rule.getThreshold()
             + " (actual " + value + ")";
    }

    /** Compare the value with the rule. */
    private  boolean matches(AlertRuleEntity r, double v) {
        return switch (r.getOp()) {
            case LT  -> v <  r.getThreshold();
            case LTE -> v <= r.getThreshold();
            case EQ  -> v == r.getThreshold();
            case GTE -> v >= r.getThreshold();
            case GT  -> v >  r.getThreshold();
             /* ---------------- CHANGE ----------------
               Triggers the alert if the value has changed from the previous one. */
            case CHANGE -> {
                String key = r.getSensorId() + '|' + r.getField();
                Double prev = lastValueCache.put(key, v);
                yield prev == null || Double.compare(prev, v) != 0;
            }
        };
    }

    private static String cacheKey(String sensor, String field) {
        return sensor + "|" + field;
    }
}
