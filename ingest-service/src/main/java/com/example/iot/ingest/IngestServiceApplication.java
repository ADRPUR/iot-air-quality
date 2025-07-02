package com.example.iot.ingest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry‑point for the IoT ingest micro‑service.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Bootstraps the Spring context.</li>
 *   <li>Scans {@code @ConfigurationProperties} classes (e.g. MQTT config).</li>
 *   <li>Enables the Spring scheduling framework for future periodic tasks
 *       (e.g. housekeeping, health‑checks).</li>
 * </ul>
 */
@SpringBootApplication
@ConfigurationPropertiesScan    // picks up MqttProperties, KafkaProperties, etc.
@EnableScheduling                // optional – can be removed if not needed later
public class IngestServiceApplication {

    /**
     * Launches the application using Spring Boot.
     *
     * @param args command‑line arguments forwarded by the JVM
     */
    public static void main(String[] args) {
        SpringApplication.run(IngestServiceApplication.class, args);
    }
}

