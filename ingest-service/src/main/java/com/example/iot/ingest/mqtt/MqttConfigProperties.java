package com.example.iot.ingest.mqtt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mqtt")
public record MqttConfigProperties(
        String broker,
        String clientId,
        String topic,
        String username,
        String password
) {}
