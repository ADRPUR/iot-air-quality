package com.example.iot.ingest.graphql.resolver;

import com.example.iot.ingest.dto.SensorValueDto;
import com.example.iot.ingest.model.SensorMeta;
import org.dataloader.DataLoader;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.concurrent.CompletableFuture;

@Controller
public class SensorValueResolver {

    /**
     * Wire up the `sensor: Sensor!` field on SensorValue via the
     * SensorMeta DataLoader.
     */
    @SchemaMapping(typeName = "SensorValue", field = "sensor")
    public CompletableFuture<SensorMeta> sensor(SensorValueDto parent,
                                                DataLoader<String, SensorMeta> sensorLoader) {
        return sensorLoader.load(parent.sensorId());
    }
}
