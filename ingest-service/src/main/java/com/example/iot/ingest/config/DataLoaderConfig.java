package com.example.iot.ingest.config;

import com.example.iot.ingest.model.SensorMeta;
import com.example.iot.ingest.repository.SensorMetaRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.BatchLoaderRegistry;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class DataLoaderConfig {

    public DataLoaderConfig(BatchLoaderRegistry registry,
                            SensorMetaRepository sensorRepository) {

        registry
                .forTypePair(String.class, SensorMeta.class)
                .registerMappedBatchLoader((sensorIdsSet, env) ->
                        Mono.fromCallable(() -> {
                                    List<String> ids = List.copyOf(sensorIdsSet);
                                    return sensorRepository.findBySensorIdIn(ids);
                                })
                                .subscribeOn(Schedulers.boundedElastic())
                                .map(list -> list.stream()
                                        .collect(Collectors.toMap(
                                                SensorMeta::getSensorId,
                                                Function.identity()
                                        ))
                                )
                );
    }
}

