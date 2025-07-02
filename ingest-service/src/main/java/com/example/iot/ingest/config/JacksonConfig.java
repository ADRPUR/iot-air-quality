package com.example.iot.ingest.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class JacksonConfig {

    private final ObjectMapper mapper;

    public JacksonConfig(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @PostConstruct
    void registerModules() {
        mapper.registerModule(new JavaTimeModule());
    }
}
