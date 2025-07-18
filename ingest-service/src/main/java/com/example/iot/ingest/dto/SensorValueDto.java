package com.example.iot.ingest.dto;

import java.time.Instant;

public record SensorValueDto(
        String sensorId,
        String field,
        double value,
        Instant ts
) {}
