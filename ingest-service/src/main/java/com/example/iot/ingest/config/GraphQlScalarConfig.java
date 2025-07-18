package com.example.iot.ingest.config;

import com.example.iot.ingest.scalar.TimestampScalar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQlScalarConfig {

    @Bean
    RuntimeWiringConfigurer timestampScalarConfigurer() {
        return wiring -> wiring.scalar(TimestampScalar.INSTANCE);
    }
}