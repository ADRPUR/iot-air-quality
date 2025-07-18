package com.example.iot.alert.domain.dto;


import java.time.Instant;
import java.util.UUID;

/** Thin transport object sent via GraphQL/REST/UI. */
public record AlertRuleDto(
        UUID     id,
        String   sensorId,
        String   field,
        Operator op,
        AlertLevel level,
        Double   threshold,
        boolean  enabled,
        Instant  created,
        Instant  updated
) {}