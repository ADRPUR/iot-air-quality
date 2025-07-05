package com.example.iot.alert.service;

import com.example.iot.alert.entity.AlertLog;
import com.example.iot.alert.entity.AlertRule;
import com.example.iot.alert.repo.AlertLogRepo;
import com.example.iot.alert.repo.AlertRuleRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertLogService {

    private final AlertLogRepo repo;

    /** Fetch all N rules */
    public List<AlertLog> findTopN(int limit) {
        return repo.findTopN(limit);
    }

    /** Save a new AlertLog */
    public void saveAlert(AlertLog alertLog) {
        repo.save(alertLog);
    }
}
