package com.example.iot.authservice.domain.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * User listing view (without password).
 * – roles contains names (e.g. “ADMIN”, “VIEWER”) for simple UX
 * – if you want more details you can change it in Set&lt;RoleDto&gt;.
 */
public record UserDto(
        UUID id,
        String email,
        String firstName,
        String lastName,
        boolean    enabled,
        Set<String> roles,
        Instant    createdAt,
        Instant updatedAt
) {}
