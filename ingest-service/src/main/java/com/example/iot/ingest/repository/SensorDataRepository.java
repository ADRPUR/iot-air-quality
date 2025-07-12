package com.example.iot.ingest.repository;

import com.example.iot.ingest.dto.SensorValueDto;
import com.example.iot.ingest.model.SensorDataEntity;
import com.example.iot.ingest.projection.SensorValueProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface SensorDataRepository
        extends JpaRepository<SensorDataEntity, UUID> {

    /**
     * Returns the latest N rows by timestamp (desc).
     */
    @Query("""
            SELECT e
            FROM   SensorDataEntity e
            ORDER  BY e.time DESC
            LIMIT  :limit
            """)
    List<SensorDataEntity> findLatest(@Param("limit") int limit);


    @Query("""
            SELECT e FROM SensorDataEntity e
            WHERE  e.sensorId = :sensor
            AND    e.field     = :field
            ORDER  BY e.time DESC
            LIMIT  :limit
            """)
    List<SensorDataEntity> findLatestBySensor(
            @Param("sensor") String sensorId,
            @Param("field") String field,
            @Param("limit") int limit);

    @Query("""
               SELECT m FROM SensorDataEntity m
               WHERE m.sensorId = :sensor
                 AND m.field     = :field
                 AND m.time BETWEEN :from AND :to
               ORDER BY m.time DESC
            """)
    List<SensorDataEntity> findBySensorFieldAndTimeBetween(
            String sensor,
            String field,
            Instant from,
            Instant to);

    @Query(value = """
            SELECT DISTINCT ON (sensor_id, field)
                   sensor_id        AS sensorId,
                   field,
                   value,
                   time             AS ts
            FROM   ingest.sensor_data
            ORDER  BY sensor_id, field, time DESC
            """,
            nativeQuery = true)
    List<SensorValueProjection> findLatestPerSensorField();
}
