package com.example.iot.alert.graphql;

import com.example.iot.alert.domain.dto.AlertLogDto;
import com.example.iot.alert.domain.dto.AlertRuleDto;
import com.example.iot.alert.domain.service.AlertLogService;
import com.example.iot.alert.domain.service.AlertRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AlertQueryController {

    private final AlertLogService logService;
    private final AlertRuleService ruleService;

    /* ---------- ALERT LOGS ------------------------------------------------ */

    /**
     * Return the latest alert logs, optionally filtered.
     *
     * @param sensorId optional filter
     * @param field    optional filter
     * @param ack      filter acknowledged / non-acknowledged
     * @param from     ISO-8601 UTC (inclusive) – optional
     * @param to       ISO-8601 UTC (inclusive) – optional
     * @param limit    max rows (default 100)
     */
    @QueryMapping
    public List<AlertLogDto> alertLogs(
            @Argument String  sensorId,
            @Argument String  field,
            @Argument Boolean ack,
            @Argument String  from,
            @Argument String  to,
            @Argument int limit) {

        Instant fromTs = from != null ? Instant.parse(from) : null;
        Instant toTs   = to   != null ? Instant.parse(to)   : null;

        return logService.findLogs(sensorId, field, ack, fromTs, toTs, limit);
    }

    /* ---------- ALERT RULES ---------------------------------------------- */

    /**
     * List all rules or filter by sensor/field/enabled.
     */
    @QueryMapping
    public List<AlertRuleDto> alertRules(
            @Argument String  sensorId,
            @Argument String  field,
            @Argument Boolean enabled) {

        return ruleService.findRules(sensorId, field, enabled);
    }
}
