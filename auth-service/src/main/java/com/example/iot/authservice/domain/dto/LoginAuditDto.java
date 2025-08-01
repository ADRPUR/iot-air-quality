package com.example.iot.authservice.domain.dto;

import java.time.Instant;
import java.util.UUID;

public record LoginAuditDto(
        UUID id,
        Instant ts,
        String email,
        String ip,
        boolean success
) {}
