package com.example.iot.ingest.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter @Setter
@ConfigurationProperties(prefix = "ingest.kafka")
public class KafkaProps {
    /** ex.: iot.raw  */
    private String topicRaw;
}
