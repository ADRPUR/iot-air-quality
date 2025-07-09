package com.example.iot.alert;

import com.example.iot.alert.entity.AlertLog;
import com.example.iot.alert.service.AlertLogService;
import com.example.iot.alert.service.AlertRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertEvaluator {

    private final ThresholdCfg cfg;
    private final JavaMailSender mailer;
    private final TelegramNotifier telegram;
    private final AlertRuleService alertRuleService;
    private final AlertLogService alertLogService;
    private final Sinks.Many<AlertLog> alertSink;

    @Value("${alert.email-to}")
    private String emailTo;

    @Value("${spring.mail.username}")
    private String emailFrom;

    /**
     * Evaluate a single sensor reading against configured thresholds.
     * If the reading exceeds its threshold, fire off notifications
     * and persist an Alert record (§2).
     */
    public void evaluate(SensorRecord rec) {
        Double limit = alertRuleService.limitFor(rec.field());
        if (limit != null && rec.value() > limit) {
            Instant ts = Instant.parse(rec.ts());
            String humanSensorTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.systemDefault())
                    .format(ts);
            Instant receivedTs = Instant.now();
            String humanReceivedTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.systemDefault())
                    .format(receivedTs);
            // build a human-readable message
            String message = "⚠️ %s %s = %.2f (>%.2f) at sensor time %s (received at %s)".formatted(
                    rec.sensor(),
                    rec.field(),
                    rec.value(),
                    limit,
                    humanSensorTime,
                    humanReceivedTime
            );

            log.warn("ALERT {}", message);

//            sendEmail(message);
            telegram.send(message);
            saveAlert(rec, limit);
        }
    }

    /**
     * Send an email via JavaMailSender.
     */
    private void sendEmail(String text) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(emailFrom);
        msg.setTo(emailTo);
        msg.setSubject("IoT Alert");
        msg.setText(text);
        mailer.send(msg);
        log.info("Sent alert email");
    }

    /**
     * Persist the alert into the database for “Alert History”.
     */
    private void saveAlert(SensorRecord rec, Double threshold) {
        AlertLog a = new AlertLog();
        a.setTs(Instant.parse(rec.ts()));
        a.setSensorId(rec.sensor());
        a.setField(rec.field());
        a.setValue(rec.value());
        a.setLimit(threshold);
        a.setChannels(new String[]{"telegram", "email"});
        alertLogService.saveAlert(a);
        alertSink.tryEmitNext(a);
        log.info("Saved alert to history: {}", a);
    }
}
