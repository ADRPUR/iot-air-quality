package com.example.iot.ingest.dto;

import com.example.iot.ingest.model.MetricAvgEntity;

import java.time.Instant;

/**
 * Read-only DTO returned de /metricsInRange (5-minute averages).
 */
public record MetricAvgDto(
        String  bucket,
        String  sensorId,
        String  field,
        Double  avgVal
) {
    public static MetricAvgDto fromEntity(MetricAvgEntity e) {
        return new MetricAvgDto(
                e.getBucket().toString(),   // ISO-8601 string
                e.getSensorId(),
                e.getField(),
                e.getAvgVal()
        );
    }
}
