package com.example.iot.ingest.repository;

import com.example.iot.ingest.model.MetricAvgEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface MetricAvgRepository extends JpaRepository<MetricAvgEntity, Instant> {

    @Query("""
            
                     SELECT m
            FROM   MetricAvgEntity m
            WHERE  m.sensorId = :sensor
              AND  m.field    = :field
              AND  m.bucket  >= :from
              AND  m.bucket  <= COALESCE(:to, m.bucket)
            ORDER  BY m.bucket DESC
            """)
    List<MetricAvgEntity> findRange(
            @Param("sensor") String sensor,
            @Param("field") String field,
            @Param("from") Instant from,
            @Param("to") Instant to,      // may be null
            Pageable page                      // ← use Pageable for LIMIT
    );
}
