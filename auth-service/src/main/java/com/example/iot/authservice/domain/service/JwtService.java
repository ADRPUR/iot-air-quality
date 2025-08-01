package com.example.iot.authservice.domain.service;

import com.example.iot.authservice.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Getter
    private final JwtProperties props;
    private SecretKey secretKey;

    /* ------------------------------------------------------------------ */
    /* Init: convert Base64 / plain text secret → SecretKey               */
    /* ------------------------------------------------------------------ */
    @PostConstruct
    void init() {
        byte[] keyBytes = props.getSecret().length() % 4 == 0
                ? Decoders.BASE64.decode(props.getSecret())
                : props.getSecret().getBytes();
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /* ------------------------------------------------------------------ */
    /* Access-token (short)                                               */
    /* ------------------------------------------------------------------ */
    public String generateAccessToken(String subject) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(props.getIssuer())
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(props.getAccessTtl())))
                .signWith(secretKey)
                .compact();
    }

    /* ------------------------------------------------------------------ */
    /* Refresh-token (long)                                               */
    /* ------------------------------------------------------------------ */
    public String generateRefreshToken(String subject) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(props.getIssuer())
                .subject(subject)
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(props.getRefreshTtl())))
                .signWith(secretKey)
                .compact();
    }

    /* ------------------------------------------------------------------ */
    /*  Parse and validate: return "sub" or throw exception → Spring Sec  */
    /* ------------------------------------------------------------------ */
    public String getSubject(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Decode and validate the JWT signature, then return all
     * {@link io.jsonwebtoken.Claims} (the full payload).
     *
     * @throws io.jsonwebtoken.JwtException if the signature is invalid,
     * the token is expired, or the structure is corrupt.
     */
    public Claims getClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

}


