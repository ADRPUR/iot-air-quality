package com.example.iot.alert.config;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.graphql.server.WebSocketGraphQlInterceptor;
import org.springframework.graphql.server.WebSocketSessionInfo;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtWsInterceptor implements WebSocketGraphQlInterceptor {
    private final ReactiveJwtDecoder jwtDecoder;

    @Override
    public @NotNull Mono<Object> handleConnectionInitialization(@NotNull WebSocketSessionInfo info,
                                                                Map<String, Object> payload) {
        var auth = (String) payload.get("Authorization");
        if (auth == null || !auth.startsWith("Bearer "))
            return Mono.error(new RuntimeException("Missing token"));

        return jwtDecoder.decode(auth.substring(7))
                .mapNotNull(jwt -> {
                    info.getAttributes().put("jwt", jwt);
                    return null;
                });
    }
}