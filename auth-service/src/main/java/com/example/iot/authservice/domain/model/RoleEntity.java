package com.example.iot.authservice.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity @Table(name = "roles", schema = "auth")
@Getter @Setter @NoArgsConstructor
public class RoleEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(unique = true, nullable = false, length = 32)
    private String name;          // ex. ADMIN, OPERATOR, VIEWER
}

