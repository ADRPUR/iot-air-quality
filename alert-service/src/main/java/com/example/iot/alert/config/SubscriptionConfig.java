package com.example.iot.alert.config;

import com.example.iot.alert.entity.AlertLog;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Configuration
public class SubscriptionConfig {
    @Bean
    public Sinks.Many<AlertLog> alertSink() {
        // replay latest event so new subscribers receive notifications
        return Sinks.many().replay().latest();
    }

    @Bean
    public Flux<AlertLog> alertPublisher(Sinks.Many<AlertLog> alertSink) {
        return alertSink.asFlux();
    }
}
