package com.example.iot.authservice.graphql;

import com.example.iot.authservice.domain.dto.LoginRequest;
import com.example.iot.authservice.domain.dto.LoginResponse;
import com.example.iot.authservice.domain.service.AuthService;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.http.server.reactive.ServerHttpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.net.InetSocketAddress;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AuthMutationController {

    private final AuthService authService;

    @MutationMapping
    public LoginResponse login(@Argument("email") String email,
                               @Argument("password") String password,
                               @ContextValue(name = "request", required = false) ServerHttpRequest req) {
        String ip = "unknown";
        if (req != null) {
            ip = Optional.ofNullable(req.getRemoteAddress())
                    .map(InetSocketAddress::getAddress)
                    .map(Object::toString)
                    .orElse("unknown");
        }

        return authService.login(new LoginRequest(email, password), ip);
    }

    @MutationMapping
    public LoginResponse refresh(@Argument("refreshToken") String refreshToken) {
        return authService.refresh(refreshToken);
    }

    @MutationMapping
    public Boolean logout(@Argument("refreshToken") String refreshToken) {
        return authService.logout(refreshToken);
    }
}
