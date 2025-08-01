package com.example.iot.authservice.domain.mapper;

import com.example.iot.authservice.domain.dto.LoginAuditDto;
import com.example.iot.authservice.domain.model.LoginAuditEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface LoginAuditMapper {

    @Mapping(target = "email", expression = "java(entity.getUser().getEmail())")
    LoginAuditDto toDto(LoginAuditEntity entity);

    @Mapping(target = "email", expression = "java(entity.getUser().getEmail())")
    List<LoginAuditDto> toDto(List<LoginAuditEntity> entities);

}
