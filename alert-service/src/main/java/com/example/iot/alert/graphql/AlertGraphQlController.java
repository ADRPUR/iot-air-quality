package com.example.iot.alert.graphql;

import com.example.iot.alert.entity.AlertLog;
import com.example.iot.alert.entity.AlertRule;
import com.example.iot.alert.repo.AlertLogRepo;
import com.example.iot.alert.repo.AlertRuleRepo;
import com.example.iot.alert.service.AlertLogService;
import com.example.iot.alert.service.AlertRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AlertGraphQlController {

    private final AlertLogService logService;
    private final AlertRuleService ruleService;

    @QueryMapping
    public List<AlertLog> alertHistory(@Argument int limit) {
        return logService.findTopN(limit);
    }

    @QueryMapping
    public List<AlertRule> alertRules() {
        return ruleService.findAllRules();
    }

    @MutationMapping
    public AlertRule updateRule(@Argument Integer id,
                                @Argument Double maxVal,
                                @Argument Boolean enabled) {
        AlertRule r = ruleService.findRuleById(id);
        r.setMaxVal(maxVal);
        r.setEnabled(enabled);
        return ruleService.saveRule(r);
    }
}
