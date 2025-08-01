package com.example.iot.authservice.domain.repo;

import com.example.iot.authservice.domain.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Main user store.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    /** Login lookup (email is unique) */
    Optional<UserEntity> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    Page<UserEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
