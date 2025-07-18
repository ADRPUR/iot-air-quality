package com.example.iot.alert.graphql;

import com.example.iot.alert.domain.dto.AlertLevel;
import com.example.iot.alert.domain.dto.AlertLogDto;
import com.example.iot.alert.domain.dto.AlertRuleDto;
import com.example.iot.alert.domain.dto.Operator;
import com.example.iot.alert.domain.service.AlertLogService;
import com.example.iot.alert.domain.service.AlertRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AlertMutationController {

    private final AlertRuleService ruleService;
    private final AlertLogService  logService;

    /* ---------- RULE CRUD ------------------------------------------------ */

    @MutationMapping
    public AlertRuleDto createRule(
            @Argument String   sensorId,
            @Argument String   field,
            @Argument Operator op,
            @Argument AlertLevel level,
            @Argument Double   threshold,
            @Argument boolean enabled) {

        return ruleService.createRule(sensorId, field, op, level, threshold, enabled);
    }

    @MutationMapping
    public AlertRuleDto updateRule(
            @Argument UUID     id,
            @Argument Operator op,
            @Argument AlertLevel level,
            @Argument Double   threshold,
            @Argument Boolean  enabled) {

        return ruleService.updateRule(id, op, level, threshold, enabled);
    }

    @MutationMapping
    public Boolean deleteRule(@Argument UUID id) {
        return ruleService.deleteRule(id);
    }

    /* ---------- ACKNOWLEDGE ALERT --------------------------------------- */

    @MutationMapping
    public AlertLogDto acknowledgeAlert(@Argument Long id) {
        return logService.acknowledge(id);
    }
}
