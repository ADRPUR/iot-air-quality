package com.example.iot.authservice.config;

import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.oauth2.server.resource.web.server.authentication.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthConverter jwtAuthConverter;
    private final JwtProperties props;

    /* === 1.  Bearer-token → Authentication  ============================ */
    @Bean
    public ServerBearerTokenAuthenticationConverter bearerTokenConverter() {
        return new ServerBearerTokenAuthenticationConverter();
    }

    /* === 2.  Stateless repo (nu scriem SecurityContext în sesiune) ===== */
    @Bean
    public NoOpServerSecurityContextRepository securityContextRepository() {
        return NoOpServerSecurityContextRepository.getInstance();
    }

    /* === 3.  Filter-chain ============================================ */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            NoOpServerSecurityContextRepository contextRepo) {

        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        http.cors(Customizer.withDefaults());

        http.securityContextRepository(contextRepo);

        http.authorizeExchange(ex -> ex
                .pathMatchers("/graphql").permitAll()
                .pathMatchers("/actuator/**").permitAll()
                .anyExchange().authenticated());

        http.oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(new ReactiveJwtAuthenticationConverterAdapter(jwtAuthConverter)))
                .bearerTokenConverter(new ServerBearerTokenAuthenticationConverter()))
                .securityContextRepository(contextRepo);

        return http.build();
    }

    /* === 4.  Decoder minim (HS256 secret) ============================= */
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        SecretKey key = Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
        return NimbusReactiveJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}

