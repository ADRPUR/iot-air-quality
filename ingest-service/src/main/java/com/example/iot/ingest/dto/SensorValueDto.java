package com.example.iot.ingest.dto;

import java.time.OffsetDateTime;

public record SensorValueDto(
        String sensorId,
        String field,
        double value,
        OffsetDateTime ts
) {}
