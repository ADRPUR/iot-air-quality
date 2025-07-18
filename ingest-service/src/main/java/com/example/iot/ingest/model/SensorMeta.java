package com.example.iot.ingest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sensor_meta", schema = "ingest")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorMeta {
    @Id
    private String sensorId;

    private String name;

    @Column(nullable = false)
    private boolean visible = true;
}
