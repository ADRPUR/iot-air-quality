package com.example.iot.alert.messaging.notifier;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotifier {

    private final JavaMailSender mailSender;

    @Value("${alert.email.enabled:true}") private  boolean enabled;
    @Value("${alert.email.from:}")        private  String  from;
    @Value("${alert.email.to:}")          private  String  to;

    /**
     * Sends a plain-text email. If email is disabled
     * (profile <dev>) or addresses are missing ➜ ignored.
     */
    public void send(String subject, String body) {
        if (!enabled || from.isBlank() || to.isBlank()) {
            log.debug("Email disabled or missing addresses – skip");
            return;
        }

        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(from);
            msg.setTo(to.split("\\s*,\\s*"));
            msg.setSubject(subject);
            msg.setText(body);

            mailSender.send(msg);
            log.debug("Email sent to {} ({} chars)", to, body.length());
        } catch (Exception ex) {
            log.error("Email send failed", ex);
        }
    }
}
