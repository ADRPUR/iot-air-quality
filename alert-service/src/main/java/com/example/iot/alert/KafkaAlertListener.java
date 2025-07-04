package com.example.iot.alert;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaAlertListener {

    private final AlertEvaluator evaluator;
    private final ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(topics = "iot.raw", groupId = "alert-service")
    public void onMessage(String json) {
        try {
            SensorRecord rec = mapper.readValue(json, SensorRecord.class);
            evaluator.evaluate(rec);
        } catch (Exception ex) {
            log.error("Failed to handle Kafka message", ex);
        }
    }
}
