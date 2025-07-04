package com.example.iot.alert;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertEvaluator {

    private final ThresholdCfg thresholds;
    private final JavaMailSender mailer;

    @Value("${alert.email-to}")   // din application.yml
    private String emailTo;

    public void evaluate(SensorRecord rec) {
        Double limit = thresholds.map().get(rec.field());
        if (limit != null && rec.value() > limit) {
            log.warn("ALERT {} {} = {}", rec.sensor(), rec.field(), rec.value());
            sendMail(rec, limit);
        }
    }

    private void sendMail(SensorRecord rec, double limit) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(emailTo);
        msg.setSubject("IoT ALERT: " + rec.field() + " = " + rec.value());
        msg.setText("Sensor %s exceeded limit %.1f at %s"
                .formatted(rec.sensor(), limit, rec.ts()));
        mailer.send(msg);
    }
}
