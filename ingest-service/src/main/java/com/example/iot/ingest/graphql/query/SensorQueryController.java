package com.example.iot.ingest.graphql.query;

import com.example.iot.ingest.dto.SensorValueDto;
import com.example.iot.ingest.model.SensorMeta;
import com.example.iot.ingest.service.MetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SensorQueryController {

    private final MetricService metricService;

    @QueryMapping
    public List<SensorValueDto> currentValues() {
        return metricService.getCurrentValues();
    }

    @QueryMapping
    public List<SensorMeta> sensors() {
        return metricService.getAllSensors();
    }
}
