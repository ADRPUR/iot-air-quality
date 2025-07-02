package com.example.iot.ingest.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sensor_data")
@Getter @Setter
public class SensorDataEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private Instant time;
    private String sensorId;
    private String field;
    private Double value;
}