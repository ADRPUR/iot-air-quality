package com.example.iot.alert.events;

import com.example.iot.alert.domain.dto.AlertLogDto;
import com.example.iot.alert.domain.service.AlertEvaluator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * Internal channel for broadcasting newly generated alerts.
 * <p>
 * - publish(dto) – called from {@link AlertEvaluator}.
 * - flux() – injected into the GraphQL/REST/WS resolver
 * to send to clients in real time.
 */
@Slf4j
@Component
public class AlertPublisher {

    /** always send the latest element + then all following */
    private final Sinks.Many<AlertLogDto> sink =
            Sinks.many().replay().latest();

    public void publish(AlertLogDto dto) {
        Sinks.EmitResult result = sink.tryEmitNext(dto);
        if (result.isFailure()) {
            log.warn("AlertPublisher emit failed: {}", result);
        }
    }

    /** "Cold" Flux – each subscriber receives the latest alert + news. */
    public Flux<AlertLogDto> flux() {
        return sink.asFlux();
    }
}
