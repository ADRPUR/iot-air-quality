package com.example.iot.ingest.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class MetricsConfig {

    @Bean
    MeterRegistryCustomizer<MeterRegistry> commonTags(
            @Value("${spring.application.name}") String appName) {

        return registry -> {
            try {
                registry.config()
                        .commonTags(
                                "application", appName,
                                "instance",    InetAddress.getLocalHost().getHostName()
                        );
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
