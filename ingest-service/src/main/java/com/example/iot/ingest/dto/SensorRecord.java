package com.example.iot.ingest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

/** DTO mapped from JSON coming via MQTT. */
public record SensorRecord(
        @JsonProperty("sensor") String sensorId,  // e.g. "dht22_1"
        String field,    // "temperature"
        double value,
        OffsetDateTime ts
) {}