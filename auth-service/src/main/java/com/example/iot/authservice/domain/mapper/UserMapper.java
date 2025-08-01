package com.example.iot.authservice.domain.mapper;

import com.example.iot.authservice.domain.dto.UserDto;
import com.example.iot.authservice.domain.model.RoleEntity;
import com.example.iot.authservice.domain.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    // Entity to DTO mapping - let MapStruct handle automatic mapping for most fields
    @Mapping(target = "roles", expression = "java(mapRoleNames(entity.getRoles()))")
    UserDto toDto(UserEntity entity);

    List<UserDto> toDto(List<UserEntity> entities);

    // DTO to Entity mapping - ignore auto-managed fields
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserEntity toEntity(UserDto dto);

    List<UserEntity> toEntity(List<UserDto> dtos);

    /* -------- helper -------------------------------------------------- */
    default Set<String> mapRoleNames(Set<RoleEntity> roles) {
        return roles == null ? Set.of()
                : roles.stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toSet());
    }
}
