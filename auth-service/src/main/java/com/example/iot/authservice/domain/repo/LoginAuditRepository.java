package com.example.iot.authservice.domain.repo;

import com.example.iot.authservice.domain.model.LoginAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Stores every authentication attempt (success / failure).
 */
@Repository
public interface LoginAuditRepository extends JpaRepository<LoginAuditEntity, UUID> {

    /** last 20 attempts for a user, newest first */
    List<LoginAuditEntity> findTop20ByUserIdOrderByTsDesc(UUID userId);
}

