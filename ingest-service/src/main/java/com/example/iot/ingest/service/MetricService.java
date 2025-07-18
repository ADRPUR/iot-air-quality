package com.example.iot.ingest.service;

import com.example.iot.ingest.dto.MetricAvgDto;
import com.example.iot.ingest.dto.SensorValueDto;
import com.example.iot.ingest.events.MetricAvgUpdate;
import com.example.iot.ingest.mapper.MetricMapper;
import com.example.iot.ingest.model.MetricAvgEntity;
import com.example.iot.ingest.model.SensorDataEntity;
import com.example.iot.ingest.model.SensorMeta;
import com.example.iot.ingest.repository.MetricAvgRepository;
import com.example.iot.ingest.repository.SensorDataRepository;
import com.example.iot.ingest.repository.SensorMetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MetricService {
    private final SensorDataRepository sensorDataRepository;
    private final MetricAvgRepository metricAvgRepository;
    private final SensorMetaRepository sensorMetaRepository;
    private final MetricMapper metricMapper;

    /**
     * Fetch the latest N SensorData rows across all sensors/fields.
     */
    public List<SensorDataEntity> getLatestMetrics(int limit) {
        return sensorDataRepository
                .findAllByOrderByTimeDesc(PageRequest.of(0, limit))
                .getContent();
    }

    /**
     * Fetch the latest N SensorData rows for a given sensorId+field.
     */
    public List<SensorDataEntity> getLatestMetricsBySensor(String sensorId, String field, int limit) {
        return sensorDataRepository
                .findBySensorIdAndFieldOrderByTimeDesc(sensorId, field, PageRequest.of(0, limit))
                .getContent();
    }

    /**
     * Fetch SensorData in [from..to] for a given sensorId+field.
     */
    public List<SensorDataEntity> getMetricsInRange(
            String sensorId,
            String field,
            Instant from,
            Instant to,
            int limit
    ) {
        return sensorDataRepository
                .findBySensorIdAndFieldAndTimeBetweenOrderByTimeDesc(
                        sensorId, field, from, to, PageRequest.of(0, limit))
                .getContent();
    }

    /**
     * Fetch 5-minute aggregates as DTOs.
     */
    public List<MetricAvgDto> getMetricsAvg5m(
            String sensorId,
            String field,
            Instant from,
            Instant to,
            int limit
    ) {
        List<MetricAvgEntity> range = metricAvgRepository.findRange(
                sensorId, field, from, to, PageRequest.of(0, limit));

        return metricMapper.toDto(range);
    }

    /**
     * Read the current_values continuous-aggregate view.
     */
    public List<SensorValueDto> getCurrentValues() {
        return metricMapper.toDtoSensor(
                sensorDataRepository.findAllCurrent());
    }

    /**
     * Fetch all known sensors (for dropdowns, etc.)
     */
    public List<SensorMeta> getAllSensors() {
        return sensorMetaRepository.findAll(Sort.by("sensorId"));
    }
}
