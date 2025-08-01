package com.example.iot.authservice.graphql;

import com.example.iot.authservice.domain.dto.LoginAuditDto;
import com.example.iot.authservice.events.LoginAuditPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
@RequiredArgsConstructor
public class AuditSubscriptionController {

    private final LoginAuditPublisher publisher;

    @SubscriptionMapping
    public Flux<LoginAuditDto> loginAudit(@Argument("success") Boolean success) {
        return publisher.flux()
                .filter(a -> success == null || a.success() == success);
    }
}
