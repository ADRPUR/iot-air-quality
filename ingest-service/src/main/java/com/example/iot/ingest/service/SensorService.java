package com.example.iot.ingest.service;

import com.example.iot.ingest.model.SensorMeta;
import com.example.iot.ingest.repository.SensorMetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SensorService {

    private final SensorMetaRepository sensorMetaRepository;

    /**
     * Return all sensors ordered by sensorId.
     */
    public List<SensorMeta> getAllSensors() {
        return sensorMetaRepository.findAll(Sort.by("sensorId"));
    }

    @Transactional
    public SensorMeta setSensorVisibility(String sensorId, boolean visible) {
        SensorMeta meta = sensorMetaRepository.findById(sensorId)
                .orElseGet(() -> new SensorMeta(sensorId, null, visible));
        meta.setVisible(visible);
        return sensorMetaRepository.save(meta);
    }

    @Transactional
    public SensorMeta renameSensor(String sensorId, String name) {
        SensorMeta meta = sensorMetaRepository.findById(sensorId)
                .orElseGet(() -> new SensorMeta(sensorId, name, true));
        meta.setName(name);
        return sensorMetaRepository.save(meta);
    }
}
