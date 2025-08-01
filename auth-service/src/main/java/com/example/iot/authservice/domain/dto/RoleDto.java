package com.example.iot.authservice.domain.dto;

import java.util.UUID;

/** Simple DTO for roles. */
public record RoleDto(
        UUID id,
        String name
) {}
