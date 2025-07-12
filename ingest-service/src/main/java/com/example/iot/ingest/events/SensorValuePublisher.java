package com.example.iot.ingest.events;

import com.example.iot.ingest.dto.SensorValueDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
@RequiredArgsConstructor
public class SensorValuePublisher {

    private final Sinks.Many<SensorValueDto> sink =
            Sinks.many().replay().latest();

    public void publish(SensorValueDto dto) {
        sink.tryEmitNext(dto);
    }

    /** Flux used by the GraphQL resolver */
    public Flux<SensorValueDto> flux() {
        return sink.asFlux();
    }
}
