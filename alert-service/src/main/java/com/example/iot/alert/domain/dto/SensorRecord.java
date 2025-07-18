package com.example.iot.alert.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SensorRecord(
        @JsonProperty("sensor") String sensorId,
        String field,
        double value,
        String ts) {
}
