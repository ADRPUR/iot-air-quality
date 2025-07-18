package com.example.iot.ingest.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sensor_data", schema = "ingest")
@Getter @Setter
public class SensorDataEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private Instant time;

    @Column(nullable = false)
    private String sensorId;

    @Column(nullable = false)
    private String field;
    private Double value;

    @Override
    public String toString() {
        return "SensorDataEntity{" +
                "id=" + id +
                ", time=" + time +
                ", sensorId='" + sensorId + '\'' +
                ", field='" + field + '\'' +
                ", value=" + value +
                '}';
    }
}