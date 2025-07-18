package com.example.iot.alert.messaging;

import com.example.iot.alert.domain.dto.SensorRecord;
import com.example.iot.alert.domain.service.AlertEvaluator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaAlertListener {

    private final AlertEvaluator evaluator;
    private final ObjectMapper   mapper;

    /** listen directly to the topic defined in application.yml */
    @KafkaListener(
            topics = "#{'${alert.kafka.topic-raw}'}",
            groupId = "${alert.kafka.group-id}"
    )
    public void onMessage(String json) {
        try {
            SensorRecord rec = mapper.readValue(json, SensorRecord.class);
            evaluator.evaluate(rec);
        } catch (Exception ex) {
            log.error("Failed to handle Kafka message", ex);
        }
    }
}


