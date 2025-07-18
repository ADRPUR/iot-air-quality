package com.example.iot.ingest.mapper;

import com.example.iot.ingest.dto.MetricAvgDto;
import com.example.iot.ingest.dto.SensorValueDto;
import com.example.iot.ingest.model.MetricAvgEntity;
import com.example.iot.ingest.projection.SensorValueProjection;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MetricMapper {

    MetricMapper INSTANCE = Mappers.getMapper(MetricMapper.class);

    /* -------- aggregates -------- */
    MetricAvgDto                       toDto(MetricAvgEntity entity);
    List<MetricAvgDto>                 toDto(List<MetricAvgEntity> list);

    /* -------- current values ----- */
    SensorValueDto                     toDto(SensorValueProjection p);
    List<SensorValueDto>               toDtoSensor(List<SensorValueProjection> list);

//    SensorDataDto          toDto(SensorDataEntity e);
//    List<SensorDataDto>    toDtoSensorData(List<SensorDataEntity> list);
}
