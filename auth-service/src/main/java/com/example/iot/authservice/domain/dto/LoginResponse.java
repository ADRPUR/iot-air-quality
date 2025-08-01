package com.example.iot.authservice.domain.dto;

/**
 * Response after authentication.
 * – accessToken → JWT (HS256) valid ~30 min
 * – refreshToken → UUID/JWT stored in DB (7 days)
 * – expiresIn → seconds until access-token expires
 */
public record LoginResponse(
        String accessToken,
        String refreshToken,
        long   expiresIn
) {}
