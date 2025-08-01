package com.example.iot.authservice.domain.repo;

import com.example.iot.authservice.domain.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Simple dictionary of roles.
 */
@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {

    Optional<RoleEntity> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
