package com.example.iot.alert.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Slf4j
@Configuration
public class MailConfig {
    /** set time-outs if the mail server is slow */
    @Bean
    JavaMailSenderImpl javaMailSender( @Value("${spring.mail.host}") String host,
                                       @Value("${spring.mail.port}") int port,
                                       @Value("${spring.mail.username}") String user,
                                       @Value("${spring.mail.password}") String pass) {

        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPort(port);
        sender.setUsername(user);
        sender.setPassword(pass);

        Properties p = sender.getJavaMailProperties();
        // time-outs în ms
        p.put("mail.smtp.connectiontimeout", 3_000);
        p.put("mail.smtp.timeout",           5_000);
        p.put("mail.smtp.writetimeout",      5_000);
        p.put("mail.smtp.auth", "true");
        p.put("mail.smtp.starttls.enable", "true");

        sender.setDefaultEncoding("UTF-8");

        log.info("JavaMailSender configured for {}", host);
        return sender;
    }
}

