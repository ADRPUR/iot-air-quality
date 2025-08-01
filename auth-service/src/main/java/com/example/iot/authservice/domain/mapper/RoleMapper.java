package com.example.iot.authservice.domain.mapper;

import com.example.iot.authservice.domain.dto.RoleDto;
import com.example.iot.authservice.domain.model.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE   // fail-fast if a field is forgotten
)
public interface RoleMapper {

    /* ---------- single ---------- */
    RoleDto toDto(RoleEntity entity);
    RoleEntity toEntity(RoleDto dto);

    /* ---------- collections ------ */
    List<RoleDto> toDto(List<RoleEntity> entities);
    List<RoleEntity> toEntity(List<RoleDto> dtos);
}