package com.example.iot.ingest.graphql;

import com.example.iot.ingest.dto.SensorValueDto;
import com.example.iot.ingest.events.MetricAvgPublisher;
import com.example.iot.ingest.events.MetricAvgUpdate;
import com.example.iot.ingest.events.SensorValuePublisher;
import com.example.iot.ingest.repository.SensorDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.time.ZoneOffset;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MetricSubscription {

    private final MetricAvgPublisher publisher;
    private final SensorValuePublisher sensorValuePublisher;
    private final SensorDataRepository repo;

    @SubscriptionMapping
    public Flux<MetricAvgUpdate> avg5mUpdated() {
        return publisher.flux();
    }

    /* -------- snapshot -------- */
    @QueryMapping
    public List<SensorValueDto> currentValues() {
        return repo.findLatestPerSensorField().stream()
                .map(p -> new SensorValueDto(
                        p.getSensorId(),
                        p.getField(),
                        p.getValue(),
                        p.getTs().atOffset(ZoneOffset.UTC)))
                .toList();
    }

    /* -------- stream -------- */
    @SubscriptionMapping
    public Flux<SensorValueDto> sensorValueUpdated(
            @Argument String sensorId,
            @Argument String field) {
        return sensorValuePublisher.flux()
                .filter(v -> sensorId == null || sensorId.equals(v.sensorId()))
                .filter(v -> field    == null || field.equals(v.field()));
    }
}
