package com.example.iot.ingest.dataloader;

import com.example.iot.ingest.model.SensorMeta;
import com.example.iot.ingest.repository.SensorMetaRepository;
import lombok.RequiredArgsConstructor;
import org.dataloader.BatchLoader;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class SensorBatchLoader implements BatchLoader<String, SensorMeta> {

    private final SensorMetaRepository repo;

    @Override
    public CompletableFuture<List<SensorMeta>> load(List<String> ids) {
        return CompletableFuture.supplyAsync(() -> repo.findBySensorIdIn((ids)));
    }

    public static final String KEY = "SENSOR_LOADER";
}
