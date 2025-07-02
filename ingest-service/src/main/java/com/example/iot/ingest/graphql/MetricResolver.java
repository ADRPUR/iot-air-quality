package com.example.iot.ingest.graphql;

import com.example.iot.ingest.model.SensorDataEntity;
import com.example.iot.ingest.repository.SensorDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MetricResolver {

    private final SensorDataRepository repo;

    @QueryMapping
    public List<SensorDataEntity> latestMetrics(@Argument("limit") Integer limit) {
        int n = (limit == null || limit <= 0) ? 20 : limit;
        return repo.findLatest(n);
    }

    @QueryMapping
    public List<SensorDataEntity> latestMetricsBySensor(
            @Argument String sensorId,
            @Argument String field,
            @Argument Integer limit) {

        int n = (limit == null || limit <= 0) ? 20 : limit;
        return repo.findLatestBySensor(sensorId, field, n);
    }
}
