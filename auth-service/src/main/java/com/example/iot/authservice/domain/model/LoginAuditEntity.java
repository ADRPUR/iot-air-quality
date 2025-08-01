package com.example.iot.authservice.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="login_audit", schema="auth",
        indexes = {
                @Index(name="idx_login_ts", columnList="ts"),
                @Index(name="idx_login_user", columnList="user_id")
        })
@Getter @Setter @NoArgsConstructor
public class LoginAuditEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private Instant ts;

    private String ip;
    private boolean success;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private UserEntity user;
}
