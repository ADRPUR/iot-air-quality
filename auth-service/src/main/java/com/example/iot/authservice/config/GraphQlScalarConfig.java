package com.example.iot.authservice.config;

import com.example.iot.authservice.scalar.TimestampScalar;
import com.example.iot.authservice.scalar.UUIDScalar;
import com.example.iot.authservice.scalar.LongScalar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQlScalarConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                .scalar(UUIDScalar.INSTANCE)
                .scalar(TimestampScalar.INSTANCE)
                .scalar(LongScalar.INSTANCE);
    }
}