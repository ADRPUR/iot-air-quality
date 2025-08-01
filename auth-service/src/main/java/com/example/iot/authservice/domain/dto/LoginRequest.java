package com.example.iot.authservice.domain.dto;

/** Login request ( GraphQL mutation: login ). */
public record LoginRequest(
        String email,
        String password
) {}
