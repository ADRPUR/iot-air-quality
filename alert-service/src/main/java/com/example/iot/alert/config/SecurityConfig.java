package com.example.iot.alert.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Flux;

import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    ReactiveJwtDecoder jwtDecoder(@Value("classpath:keys/public.pem") RSAPublicKey key) {
        var decoder = NimbusReactiveJwtDecoder.withPublicKey(key).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer("http://localhost:8081")); // iss din token
        return decoder;
    }

    @Bean
    ReactiveJwtAuthenticationConverter jwtAuthConverter() {
        var authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("roles");
        authoritiesConverter.setAuthorityPrefix("ROLE_");   // 'ADMIN' → ROLE_ADMIN

        var reactive = new ReactiveJwtAuthenticationConverter();

        reactive.setJwtGrantedAuthoritiesConverter(jwt ->
                Flux.fromIterable(authoritiesConverter.convert(jwt)));

        return reactive;
    }

    @Bean
    SecurityWebFilterChain api(ServerHttpSecurity http) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(Customizer.withDefaults())

                .authorizeExchange(ex -> ex
                        // handshake + introspecţie WS
                        .pathMatchers(HttpMethod.GET, "/graphql").permitAll()
                        // login / register / refresh (POST)
                        .pathMatchers(HttpMethod.POST, "/graphql").permitAll()
                        // pre-flight
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        .anyExchange().authenticated())

                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter())));

        return http.build();
    }
}
