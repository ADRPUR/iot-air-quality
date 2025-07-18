package com.example.iot.ingest.repository;

import com.example.iot.ingest.model.SensorDataEntity;
import com.example.iot.ingest.projection.SensorValueProjection;
import io.micrometer.core.annotation.Timed;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * ARead-only access to raw data from ingest.sensor_data + current view.
 * We use {@link Pageable} (PageRequest.of(0, n)) instead of LIMIT ➜
 * dialect-independent (PostgreSQL, H2, Oracle).
 */
public interface SensorDataRepository extends JpaRepository<SensorDataEntity, UUID> {

    /* ---------- newest N global rows ---------- */
    Page<SensorDataEntity> findAllByOrderByTimeDesc(Pageable pageable);

    /* ---------- newest N rows for (sensor, field) ---------- */
    Page<SensorDataEntity> findBySensorIdAndFieldOrderByTimeDesc(
            String  sensorId,
            String  field,
            Pageable pageable);

    /* ---------- interval [from, to] for (sensor, field) ---------- */
    Page<SensorDataEntity> findBySensorIdAndFieldAndTimeBetweenOrderByTimeDesc(
            String  sensorId,
            String  field,
            Instant from,
            Instant to,
            Pageable pageable);

    /* ---------- projection from materialized view current_values ---------- */
    @Query(value = """
            SELECT sensor_id AS sensorId,
                   field,
                   value,
                   ts
            FROM ingest.current_values
            """, nativeQuery = true)
    List<SensorValueProjection> findAllCurrent();

}
