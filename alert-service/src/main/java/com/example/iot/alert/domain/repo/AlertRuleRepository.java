package com.example.iot.alert.domain.repo;

import com.example.iot.alert.domain.model.AlertRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * CRUD for alert rule definitions.
 */
@Repository
public interface AlertRuleRepository
        extends JpaRepository<AlertRuleEntity, UUID>,
        JpaSpecificationExecutor<AlertRuleEntity> {

    List<AlertRuleEntity> findByEnabledTrue();
    List<AlertRuleEntity> findBySensorIdAndField(String sensorId, String field);
    List<AlertRuleEntity> findByEnabledTrueOrderBySensorIdAscFieldAsc();
}


