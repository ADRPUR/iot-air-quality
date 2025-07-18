package com.example.iot.alert.config;

import com.example.iot.alert.scalar.TimestampScalar;
import com.example.iot.alert.scalar.UUIDScalar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

/**
 * Registers custom GraphQL scalar types.
 */
@Configuration
public class GraphQlConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                .scalar(UUIDScalar.INSTANCE)
                .scalar(TimestampScalar.INSTANCE);
    }
}

