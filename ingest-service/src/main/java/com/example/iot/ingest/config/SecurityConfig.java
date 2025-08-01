package com.example.iot.ingest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Security configuration for ingest-service.
 * Allows public access to GraphQL endpoints for development.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .cors(ServerHttpSecurity.CorsSpec::and) // Enable CORS
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Disable CSRF for GraphQL
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/graphql", "/graphql/**").permitAll() // Allow GraphQL endpoints
                        .pathMatchers("/actuator/**").permitAll() // Allow actuator endpoints
                        .anyExchange().permitAll() // Allow all other endpoints for now
                )
                .build();
    }
}
