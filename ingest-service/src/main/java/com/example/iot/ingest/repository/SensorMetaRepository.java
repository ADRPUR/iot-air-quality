package com.example.iot.ingest.repository;

import com.example.iot.ingest.model.SensorMeta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  SensorMetaRepository extends JpaRepository<SensorMeta, String> {
}
