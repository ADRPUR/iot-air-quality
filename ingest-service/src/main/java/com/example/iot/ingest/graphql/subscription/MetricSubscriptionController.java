package com.example.iot.ingest.graphql.subscription;

import com.example.iot.ingest.events.MetricAvgPublisher;
import com.example.iot.ingest.events.MetricAvgUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
@RequiredArgsConstructor
public class MetricSubscriptionController {

    private final MetricAvgPublisher metricAvgPublisher;

    /**
     * Fires every time a new 5-minute aggregate is published.
     */
    @SubscriptionMapping
    public Flux<MetricAvgUpdate> avg5mUpdated(
            @Argument("sensorId") String sensorId,
            @Argument("field")    String field) {

        return metricAvgPublisher.flux()
                .filter(u -> sensorId == null || u.sensorId().equals(sensorId))
                .filter(u -> field == null || u.field().equals(field));
    }
}
