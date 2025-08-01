package com.example.iot.authservice.domain.service;

import com.example.iot.authservice.domain.dto.UserDto;
import com.example.iot.authservice.domain.mapper.UserMapper;
import com.example.iot.authservice.domain.model.RoleEntity;
import com.example.iot.authservice.domain.model.UserEntity;
import com.example.iot.authservice.domain.repo.RoleRepository;
import com.example.iot.authservice.domain.repo.UserRepository;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository  userRepo;
    private final RoleRepository  roleRepo;
    private final UserMapper      mapper;
    private final PasswordEncoder encoder;

    /* ====================================================================
       CREATE
       ==================================================================== */
    @Transactional
    public UserDto create(UserDto dto, Set<String> roleNames, String rawPassword) {
        log.info("UserService.create() called with dto: {}", dto);
        log.info("DTO email: {}, firstName: {}, lastName: {}", dto.email(), dto.firstName(), dto.lastName());

        if (userRepo.existsByEmailIgnoreCase(dto.email())) {
            throw new IllegalArgumentException("Email already used");
        }
        UserEntity u = mapper.toEntity(dto);
        log.info("After mapper.toEntity(), UserEntity: id={}, email={}, firstName={}, lastName={}",
                 u.getId(), u.getEmail(), u.getFirstName(), u.getLastName());

        u.setPassword(encoder.encode(rawPassword));
        u.setRoles(resolveRoles(roleNames));
        u.setCreatedAt(Instant.now());
        u.setUpdatedAt(Instant.now());
        u.setEnabled(true);

        log.info("Before save, UserEntity: id={}, email={}, firstName={}, lastName={}",
                 u.getId(), u.getEmail(), u.getFirstName(), u.getLastName());

        return mapper.toDto(userRepo.save(u));
    }

    /* ====================================================================
       READ
       ==================================================================== */
    public Optional<UserDto> findByEmail(@Email String email) {
        return userRepo.findByEmailIgnoreCase(email).map(mapper::toDto);
    }

    public List<UserDto> findAll(int limit) {
        return mapper.toDto(userRepo
                .findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit)).getContent());
    }

    public Optional<UserDto> findById(UUID id) {
        return userRepo.findById(id).map(mapper::toDto);
    }

    /* ====================================================================
       UPDATE  – patch only the given fields
       ==================================================================== */
    @Transactional
    public Optional<UserDto> update(UUID id,
                                    String firstName,
                                    String lastName,
                                    Boolean enabled,
                                    Set<String> roleNames) {

        return userRepo.findById(id).map(u -> {

            if (firstName != null) u.setFirstName(firstName);
            if (lastName  != null) u.setLastName(lastName);
            if (enabled   != null) u.setEnabled(enabled);
            if (roleNames != null) u.setRoles(resolveRoles(roleNames));

            u.setUpdatedAt(Instant.now());
            return mapper.toDto(u);              // “dirty” entity: Hibernate va face update la commit
        });
    }

    /* ====================================================================
       DELETE
       ==================================================================== */
    @Transactional
    public boolean delete(UUID id) {
        if (userRepo.existsById(id)) {
            userRepo.deleteById(id);
            return true;
        }
        return false;
    }

    /* ====================================================================
       CHANGE PASSWORD (administrative or self-service)
       ==================================================================== */
    @Transactional
    public boolean changePassword(UUID id, String newRawPassword) {
        return userRepo.findById(id).map(u -> {
            u.setPassword(encoder.encode(newRawPassword));
            u.setUpdatedAt(Instant.now());
            return true;
        }).orElse(false);
    }

    /* ====================================================================
       HELPERS
       ==================================================================== */
    private Set<RoleEntity> resolveRoles(Set<String> names) {
        if (names == null || names.isEmpty()) return Collections.emptySet();
        return names.stream()
                .map(name -> roleRepo.findByNameIgnoreCase(name)
                        .orElseThrow(() -> new IllegalArgumentException("Role " + name + " not found")))
                .collect(Collectors.toSet());
    }
}
