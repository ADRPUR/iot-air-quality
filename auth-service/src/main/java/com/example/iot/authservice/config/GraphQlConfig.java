package com.example.iot.authservice.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Exposes CORS policy for both HTTP and WebSocket
 * /graphql (Spring for GraphQL automatically registers the same path).
 */
@Configuration
public class GraphQlConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(
                List.of("https://app.example.com", "http://localhost:5173"));
        cfg.addAllowedMethod("*");   // GET, POST, WS handshake etc.
        cfg.addAllowedHeader("*");
        cfg.setAllowCredentials(true);   // if you send Cookie / Auth-header

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // both HTTP ("/graphql") and WS ("/graphql") go through the same path pattern
        source.registerCorsConfiguration("/graphql/**", cfg);

        return source;
    }
}


