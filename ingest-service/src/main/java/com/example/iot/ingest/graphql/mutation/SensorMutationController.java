package com.example.iot.ingest.graphql.mutation;

import com.example.iot.ingest.model.SensorMeta;
import com.example.iot.ingest.service.SensorService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class SensorMutationController {

    private final SensorService sensorService;

    @MutationMapping
    public SensorMeta setSensorVisibility(
            @Argument String sensorId,
            @Argument Boolean visible
    ) {
        return sensorService.setSensorVisibility(sensorId, visible);
    }

    @MutationMapping
    public SensorMeta renameSensor(
            @Argument String sensorId,
            @Argument String name
    ) {
        return sensorService.renameSensor(sensorId, name);
    }
}
