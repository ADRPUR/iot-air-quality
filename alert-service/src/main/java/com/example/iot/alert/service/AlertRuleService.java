package com.example.iot.alert.service;

import com.example.iot.alert.entity.AlertRule;
import com.example.iot.alert.repo.AlertRuleRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertRuleService {

    private final AlertRuleRepo repo;

    /** Fetch all rules */
    public List<AlertRule> findAllRules() {
        return repo.findAll();
    }

    /** Update one rule */
    @Transactional
    public AlertRule updateRule(int id, double maxVal, boolean enabled) {
        AlertRule rule = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rule not found: " + id));
        rule.setMaxVal(maxVal);
        rule.setEnabled(enabled);
        return repo.save(rule);
    }

    /** Find rule by ID */
    public AlertRule findRuleById(int id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rule not found: " + id));
    }

    /** Save a rule */
    @Transactional
    public AlertRule saveRule(AlertRule rule) {
        return repo.save(rule);
    }

    /** Helper for evaluator */
    public Double limitFor(String field) {
        return repo.findByFieldNameAndEnabledIsTrue(field)
                .map(AlertRule::getMaxVal)
                .orElse(null);
    }
}
