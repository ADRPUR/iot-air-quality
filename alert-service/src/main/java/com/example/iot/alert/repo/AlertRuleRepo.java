package com.example.iot.alert.repo;

import com.example.iot.alert.entity.AlertRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlertRuleRepo extends JpaRepository<AlertRule, Integer> {
    Optional<AlertRule> findByFieldNameAndEnabledIsTrue(String field);
}
