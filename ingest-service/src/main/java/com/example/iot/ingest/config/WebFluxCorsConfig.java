package com.example.iot.ingest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class WebFluxCorsConfig implements WebFluxConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/graphql")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
