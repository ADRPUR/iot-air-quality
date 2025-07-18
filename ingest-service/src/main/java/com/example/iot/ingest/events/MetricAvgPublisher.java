package com.example.iot.ingest.events;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class MetricAvgPublisher {

    private final Sinks.Many<MetricAvgUpdate> sink =
            Sinks.many().replay().latest();

    /* EXISTENT ---------------------------------------------------------- */
    public void publish(String sensor, String field) {
        sink.tryEmitNext(new MetricAvgUpdate(sensor, field));
    }

    /* NEW – accept  DTO directly -------------------------------------- */
    public void publish(MetricAvgUpdate update) {
        sink.tryEmitNext(update);
    }

    public Flux<MetricAvgUpdate> flux() {
        return sink.asFlux();
    }
}
