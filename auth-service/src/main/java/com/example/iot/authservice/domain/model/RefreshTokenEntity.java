package com.example.iot.authservice.domain.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;

import java.util.UUID;

@Entity @Table(name="refresh_tokens", schema="auth",
        indexes = @Index(name="idx_refresh_exp", columnList="expiry"))
@Getter @Setter @NoArgsConstructor
public class RefreshTokenEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private UserEntity user;

    @Column(nullable = false)
    private Instant expiry;
}
