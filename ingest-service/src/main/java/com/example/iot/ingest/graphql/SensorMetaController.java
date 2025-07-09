package com.example.iot.ingest.graphql;

import com.example.iot.ingest.model.SensorMeta;
import com.example.iot.ingest.repository.SensorMetaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SensorMetaController {
    private final SensorMetaRepository repo;

    /* ---------- QUERY ---------- */
    @QueryMapping
    public List<SensorMeta> sensors() {
        return repo.findAll(Sort.by("sensorId"));
    }

    /* ---------- MUTATIONS ---------- */
    @MutationMapping
    @Transactional
    public SensorMeta setSensorVisibility(@Argument String sensorId,
                                          @Argument boolean visible) {
        SensorMeta meta = repo.findById(sensorId)
                .orElseGet(() -> new SensorMeta(sensorId, null, visible));
        meta.setVisible(visible);
        return repo.save(meta);
    }

    @MutationMapping
    @Transactional
    public SensorMeta renameSensor(@Argument String sensorId,
                                   @Argument String name) {
        SensorMeta meta = repo.findById(sensorId)
                .orElseGet(() -> new SensorMeta(sensorId, name, true));
        meta.setName(name);
        return repo.save(meta);
    }
}
