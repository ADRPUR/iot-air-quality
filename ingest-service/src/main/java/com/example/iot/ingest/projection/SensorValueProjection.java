package com.example.iot.ingest.projection;

import java.time.Instant;

public interface SensorValueProjection {
    String  getSensorId();
    String  getField();
    Double  getValue();
    Instant getTs();
}
