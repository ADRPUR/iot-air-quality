package com.example.iot.alert.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                        .addMapping("/graphql")          // GraphQL HTTP endpoint
                        .allowedOrigins("http://localhost:5173")  // React dev-server
                        .allowedMethods("POST", "OPTIONS")
                        .allowCredentials(true);
            }
        };
    }
}
