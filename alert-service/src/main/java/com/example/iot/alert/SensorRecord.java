package com.example.iot.alert;

public record SensorRecord(
        String sensor,
        String field,
        double value,
        String ts) {
}
