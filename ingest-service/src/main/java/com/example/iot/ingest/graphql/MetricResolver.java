package com.example.iot.ingest.graphql;

import com.example.iot.ingest.dto.MetricAvgDto;
import com.example.iot.ingest.model.SensorDataEntity;
import com.example.iot.ingest.repository.MetricAvgRepository;
import com.example.iot.ingest.repository.SensorDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MetricResolver {

    private final SensorDataRepository repo;
    private final MetricAvgRepository metricAvgRepo;

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

    @QueryMapping
    public List<SensorDataEntity> metricsInRange(@Argument String sensorId,
                                                 @Argument String field,
                                                 @Argument String from,
                                                 @Argument String to) {
        Instant tFrom = Instant.parse(from);
        Instant upper = (to != null ? Instant.parse(to) : Instant.now());
        return repo.findBySensorFieldAndTimeBetween(sensorId, field, tFrom, upper);
    }

    @QueryMapping
    public List<MetricAvgDto> metricsAvg5m(
            @Argument String sensorId,
            @Argument String field,
            @Argument String from,
            @Argument String to,
            @Argument Integer limit) {

        var fromTs = Instant.parse(from);
        Instant toTs = (to != null) ? Instant.parse(to) : null;

        int size = (limit == null || limit <= 0) ? 500 : limit;
        return metricAvgRepo.findRange(sensorId, field, fromTs, toTs,
                PageRequest.of(0, size))
            .stream()
                 .map(p -> new MetricAvgDto(
                         p.getBucket().toString(), p.getSensorId(), p.getField(), p.getAvgVal()))
                 .toList();
    }
}
