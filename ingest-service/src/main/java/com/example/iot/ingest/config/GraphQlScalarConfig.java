package com.example.iot.ingest.config;

import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQlScalarConfig {

    @Bean
    RuntimeWiringConfigurer timestampScalar() {
        GraphQLScalarType timestamp = GraphQLScalarType
                .newScalar(ExtendedScalars.DateTime)
                .name("Timestamp")
                .description("ISO-8601 instant in UTC")
                .build();
        return wiring -> wiring.scalar(timestamp);
    }
}