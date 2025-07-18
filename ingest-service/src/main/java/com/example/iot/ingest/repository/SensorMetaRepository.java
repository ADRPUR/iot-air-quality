package com.example.iot.ingest.repository;

import com.example.iot.ingest.model.SensorMeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface  SensorMetaRepository extends JpaRepository<SensorMeta, String> {
    List<SensorMeta> findBySensorIdIn(List<String> sensorIds);
}
