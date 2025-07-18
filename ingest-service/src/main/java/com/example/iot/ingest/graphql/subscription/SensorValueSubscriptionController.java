package com.example.iot.ingest.graphql.subscription;

import com.example.iot.ingest.dto.SensorValueDto;
import com.example.iot.ingest.events.SensorValuePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
@RequiredArgsConstructor
public class SensorValueSubscriptionController {

    private final SensorValuePublisher sensorValuePublisher;

    /**
     * Streams individual sensor‐value updates.
     * Both arguments are nullable; if you don’t pass them in your GraphQL subscription,
     * you get all updates.
     */
    @SubscriptionMapping
    public Flux<SensorValueDto> sensorValueUpdated(
            @Argument String sensorId,
            @Argument String field
    ) {
        return sensorValuePublisher.flux()
                .filter(v -> sensorId == null || sensorId.equals(v.sensorId()))
                .filter(v -> field    == null || field.equals(v.field()));
    }
}
