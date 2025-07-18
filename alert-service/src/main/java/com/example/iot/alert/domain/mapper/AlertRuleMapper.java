package com.example.iot.alert.domain.mapper;

import com.example.iot.alert.domain.dto.AlertRuleDto;
import com.example.iot.alert.domain.model.AlertRuleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AlertRuleMapper {

    AlertRuleMapper INSTANCE = Mappers.getMapper(AlertRuleMapper.class);

    /* ───── Entity → DTO ─────────────────────────────────────────────── */
    AlertRuleDto toDto(AlertRuleEntity entity);
    List<AlertRuleDto> toDto(List<AlertRuleEntity> list);

    /* ───── DTO → Entity (for create / update) ───────────────────────── */
    @Mapping(target = "created", ignore = true) // managed by DB
    @Mapping(target = "updated", ignore = true)
    AlertRuleEntity toEntity(AlertRuleDto dto);
}
