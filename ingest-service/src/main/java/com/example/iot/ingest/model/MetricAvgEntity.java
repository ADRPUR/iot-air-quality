package com.example.iot.ingest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.time.Instant;

@Entity
@Table(name = "sensor_avg_5m", schema = "ingest")
@Immutable
@Getter
public class MetricAvgEntity {

    @Id
    @Column(name = "bucket")
    private Instant bucket;

    private String sensorId;
    private String field;

    @Column(name = "avg_val")
    private Double avgVal;

}

