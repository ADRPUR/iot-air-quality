package com.example.iot.ingest.graphql.resolver;

import com.example.iot.ingest.dto.MetricAvgDto;
import com.example.iot.ingest.events.MetricAvgUpdate;
import com.example.iot.ingest.model.SensorMeta;
import org.dataloader.DataLoader;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.concurrent.CompletableFuture;

@Controller
public class MetricAvgResolver {

    /**
     * Resolves the "sensor" field on MetricAvg by deferring to the
     * SensorMeta DataLoader.
     */
    @SchemaMapping(typeName = "MetricAvg", field = "sensor")
    public CompletableFuture<SensorMeta> sensor(MetricAvgDto parent,
                                                DataLoader<String, SensorMeta> sensorLoader) {
        // parent.sensorId() is the key into your SensorMetaRepository
        return sensorLoader.load(parent.sensorId());
    }

    @SchemaMapping(typeName = "MetricAvgUpdate", field = "sensor")
    public CompletableFuture<SensorMeta> sensor(MetricAvgUpdate parent,
                                                DataLoader<String, SensorMeta> loader) {
        return loader.load(parent.sensorId());
    }
}
