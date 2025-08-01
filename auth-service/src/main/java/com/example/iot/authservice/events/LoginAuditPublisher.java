package com.example.iot.authservice.events;

import com.example.iot.authservice.domain.dto.LoginAuditDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * Publish each newly created {@link LoginAuditDto}.
 * <p>
 * – multicast + back-pressure buffer ⇒ send each subscriber all
 * elements, without losing messages if the downstream is slower.<br>
 * – thread-safe: Reactor {@code Sinks} handles concurrency.
 */
@Slf4j
@Component
public class LoginAuditPublisher {

    /** Hot publisher – each subscriber receives the “live” feed */
    private final Sinks.Many<LoginAuditDto> sink =
            Sinks.many().multicast().onBackpressureBuffer();

    /** Called by LoginAuditService after it saves the entity. */
    public void publish(LoginAuditDto dto) {
        Sinks.EmitResult res = sink.tryEmitNext(dto);
        if (res.isFailure()) {
            log.warn("LoginAudit publish failed: {}", res);
        }
    }

    /** Flux exposed to GraphQL Subscription. */
    public Flux<LoginAuditDto> flux() {
        return sink.asFlux();
    }
}
