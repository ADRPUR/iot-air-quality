package com.example.iot.ingest.events;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class MetricAvgPublisher {
    private final Sinks.Many<MetricAvgUpdate> sink =
            Sinks.many().replay().latest();

    public void publish(String sensor, String field) {
        sink.tryEmitNext(new MetricAvgUpdate(sensor, field));
    }

    public Flux<MetricAvgUpdate> flux() {
        return sink.asFlux();
    }
}
