package com.example.iot.ingest.graphql.query;

import com.example.iot.ingest.dto.MetricAvgDto;
import com.example.iot.ingest.model.SensorDataEntity;
import com.example.iot.ingest.service.MetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MetricQueryController {

    private final MetricService metricService;

    @QueryMapping
    public List<SensorDataEntity> latestMetrics(
            @Argument("limit") Integer limit
    ) {
        return metricService.getLatestMetrics(limit);
    }

    @QueryMapping
    public List<SensorDataEntity> latestMetricsBySensor(
            @Argument String sensorId,
            @Argument String field,
            @Argument("limit") Integer limit
    ) {
        return metricService.getLatestMetricsBySensor(sensorId, field, limit);
    }

    @QueryMapping
    public List<SensorDataEntity> metricsInRange(
            @Argument String sensorId,
            @Argument String field,
            @Argument String from,
            @Argument("to") String to,
            @Argument("limit") Integer limit
    ) {
        Instant fromTs = Instant.parse(from);
        Instant toTs   = to != null ? Instant.parse(to) : null;
        return metricService.getMetricsInRange(sensorId, field, fromTs, toTs, limit);
    }

    @QueryMapping
    public List<MetricAvgDto> metricsAvg5m(
            @Argument String sensorId,
            @Argument String field,
            @Argument String from,
            @Argument("to") String to,
            @Argument("limit") Integer limit
    ) {
        Instant fromTs = Instant.parse(from);
        Instant toTs   = to != null ? Instant.parse(to) : null;
        return metricService.getMetricsAvg5m(sensorId, field, fromTs, toTs, limit);
    }
}
