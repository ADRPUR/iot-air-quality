package com.example.iot.ingest.model;

import java.time.OffsetDateTime;

/** DTO mapped from JSON coming via MQTT. */
public record SensorRecord(
        String sensor,   // e.g. "dht22_1"
        String field,    // "temperature"
        double value,
        OffsetDateTime ts
) {}