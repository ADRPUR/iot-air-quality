package com.example.iot.ingest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * CORS configuration for WebFlux (Reactive Spring Boot).
 * Allows frontend at localhost:5173 to access GraphQL endpoints.
 */
@Configuration
public class CorsConfig {

    @Bean
    @Order(-100) // High priority before security
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOriginPatterns(List.of("http://localhost:*")); // Allow any localhost port
        corsConfig.addAllowedOrigin("http://localhost:5173");
        corsConfig.addAllowedOrigin("http://localhost:3000");
        corsConfig.addAllowedMethod("GET");
        corsConfig.addAllowedMethod("POST");
        corsConfig.addAllowedMethod("OPTIONS");
        corsConfig.addAllowedHeader("*");
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(3600L); // Cache preflight response

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Register for all paths that might be used
        source.registerCorsConfiguration("/**", corsConfig);
        source.registerCorsConfiguration("/graphql", corsConfig);
        source.registerCorsConfiguration("/graphql/**", corsConfig);

        return new CorsWebFilter(source);
    }
}