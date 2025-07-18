package com.example.iot.alert.domain.repo;

import com.example.iot.alert.domain.model.AlertLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertLogRepository
        extends JpaRepository<AlertLogEntity, Long>,
        JpaSpecificationExecutor<AlertLogEntity> {

    List<AlertLogEntity> findAllByOrderByCreatedDesc(org.springframework.data.domain.Pageable pg);
    List<AlertLogEntity> findBySensorIdAndFieldOrderByCreatedDesc(
            String sensorId, String field, org.springframework.data.domain.Pageable pg);
    List<AlertLogEntity> findByAckFalseOrderByCreatedDesc();
}

