package com.example.iot.ingest.model;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.time.Instant;

@Entity
@Table(name = "sensor_avg_5m", schema = "ingest")
@Immutable
@IdClass(MetricAvgKey.class)
@Getter
@Cacheable
public class MetricAvgEntity {

    @Id
    @Column(name = "bucket")
    private Instant bucket;

    @Id
    @Column(nullable = false)
    private String sensorId;

    @Id
    @Column(nullable = false)
    private String field;

    @Column(name = "avg_val")
    private Double avgVal;

}

