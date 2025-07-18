package com.example.iot.ingest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

/* ---------- helper key ---------- */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MetricAvgKey implements Serializable {
    private Instant bucket;
    private String  sensorId;
    private String  field;

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}