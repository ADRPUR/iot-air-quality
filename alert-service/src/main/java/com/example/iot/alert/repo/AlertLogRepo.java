package com.example.iot.alert.repo;

import com.example.iot.alert.entity.AlertLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AlertLogRepo extends JpaRepository<AlertLog, UUID> {
    @Query(value = "SELECT * FROM alert.alert_log ORDER BY ts DESC LIMIT :n", nativeQuery = true)
    List<AlertLog> findTopN(@Param("n") int n);
}