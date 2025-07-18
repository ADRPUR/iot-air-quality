package com.example.iot.alert.domain.service;

import com.example.iot.alert.domain.dto.AlertLevel;
import com.example.iot.alert.domain.dto.AlertLogDto;
import com.example.iot.alert.domain.mapper.AlertLogMapper;
import com.example.iot.alert.domain.model.AlertLogEntity;
import com.example.iot.alert.domain.repo.AlertLogRepository;
import com.example.iot.alert.events.AlertPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertLogService {

    private final AlertLogRepository repo;
    private final AlertLogMapper mapper;
    private final AlertPublisher publisher;

    /* ------------------------------------------------------------------
       1) Creating a new log (called by AlertEvaluator)
    ------------------------------------------------------------------ */
    @Transactional
    public AlertLogDto createLog(String sensorId,
                                 String field,
                                 String ruleCode,
                                 AlertLevel level,
                                 String message) {

        AlertLogEntity entity = AlertLogEntity.builder()
                .created(Instant.now())
                .sensorId(sensorId)
                .field(field)
                .ruleCode(ruleCode)
                .level(level)
                .message(message)
                .build();

        AlertLogEntity saved = repo.save(entity);
        AlertLogDto    dto   = mapper.toDto(saved);

        publisher.publish(dto);   // push to subscribers

        log.debug("Alert logged & published: {}", dto);
        return dto;
    }

    /* ------------------------------------------------------------------
       2) ACK / un-ACK
    ------------------------------------------------------------------ */

    @Transactional
    public AlertLogDto acknowledge(Long id) {
        return acknowledge(id, true)
                .orElseThrow(() ->
                        new IllegalArgumentException("Alert not found: " + id));
    }

    @Transactional
    public Optional<AlertLogDto> acknowledge(Long id, boolean ack) {
        return repo.findById(id)
                .map(e -> {
                    e.setAck(ack);
                    e.setAckTime(ack ? Instant.now() : null);
                    AlertLogDto dto = mapper.toDto(e);
                    log.info("Alert {} {}", id, ack ? "ACK" : "un-ACK");
                    return dto;
                });
    }

    /* ------------------------------------------------------------------
       3) Quick queries
    ------------------------------------------------------------------ */

    public List<AlertLogDto> latest(int limit) {
        return mapper.toDto(
                repo.findAllByOrderByCreatedDesc(PageRequest.of(0, limit)));
    }

    public List<AlertLogDto> findBySensorAndField(String sensorId,
                                                  String field,
                                                  int limit) {
        return mapper.toDto(
                repo.findBySensorIdAndFieldOrderByCreatedDesc(
                        sensorId, field, PageRequest.of(0, limit)));
    }

    public List<AlertLogDto> findUnacknowledged() {
        return mapper.toDto(
                repo.findByAckFalseOrderByCreatedDesc());
    }

    public List<AlertLogDto> findLogs(String   sensorId,
                                      String   field,
                                      Boolean  ack,
                                      Instant  from,
                                      Instant  to,
                                      int      limit) {

        Specification<AlertLogEntity> spec = Specification.where(null);

        if (sensorId != null) {
            spec = spec.and((r, q, b) -> b.equal(r.get("sensorId"), sensorId));
        }
        if (field != null) {
            spec = spec.and((r, q, b) -> b.equal(r.get("field"), field));
        }
        if (ack != null) {
            spec = spec.and((r, q, b) -> b.equal(r.get("ack"), ack));
        }
        if (from != null) {
            spec = spec.and((r, q, b) -> b.greaterThanOrEqualTo(r.get("created"), from));
        }
        if (to != null) {
            spec = spec.and((r, q, b) -> b.lessThanOrEqualTo(r.get("created"), to));
        }

        var page = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "created"));
        return mapper.toDto(repo.findAll(spec, page).getContent());
    }
}
