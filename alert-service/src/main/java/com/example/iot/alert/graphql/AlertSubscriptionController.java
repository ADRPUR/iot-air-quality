package com.example.iot.alert.graphql;

import com.example.iot.alert.domain.dto.AlertLogDto;
import com.example.iot.alert.events.AlertPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
@RequiredArgsConstructor
public class AlertSubscriptionController {

    private final AlertPublisher publisher;

    /**
     * Live stream of newly-triggered alerts.
     */
    @SubscriptionMapping
    public Flux<AlertLogDto> alertTriggered(
            @Argument String sensorId,
            @Argument String field) {

        return publisher.flux()
                .filter(a -> sensorId == null || a.getSensorId().equals(sensorId))
                .filter(a -> field    == null || a.getField().equals(field));
    }
}
