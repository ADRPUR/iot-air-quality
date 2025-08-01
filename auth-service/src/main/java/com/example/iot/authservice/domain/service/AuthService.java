package com.example.iot.authservice.domain.service;

import com.example.iot.authservice.domain.dto.LoginRequest;
import com.example.iot.authservice.domain.dto.LoginResponse;
import com.example.iot.authservice.domain.dto.UserDto;
import com.example.iot.authservice.domain.mapper.UserMapper;
import com.example.iot.authservice.domain.model.RoleEntity;
import com.example.iot.authservice.domain.model.UserEntity;
import com.example.iot.authservice.domain.repo.RoleRepository;
import com.example.iot.authservice.domain.repo.UserRepository;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository         userRepo;
    private final RoleRepository         roleRepo;
    private final PasswordEncoder        encoder;
    private final JwtService             jwtService;
    private final RefreshTokenService    refreshService;
    private final LoginAuditService      auditService;
    private final UserMapper             mapper;

    /* ===================================================================
      REGISTER – self-service create account (default role USER)
      =================================================================== */
    @Transactional
    public UserDto register(@Email String email,
                            String password,
                            String firstName,
                            String lastName) {

        if (userRepo.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("E-mail already registered");
        }

        RoleEntity userRole = roleRepo.findByNameIgnoreCase("USER")
                .orElseThrow(() -> new IllegalStateException("Missing USER role"));

        UserEntity entity = UserEntity.builder()
                .id(UUID.randomUUID())
                .email(email.toLowerCase())
                .password(encoder.encode(password))
                .firstName(firstName)
                .lastName(lastName)
                .roles(Set.of(userRole))
                .enabled(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        return mapper.toDto(userRepo.save(entity));
    }


    /* ------------------------------------------------ LOGIN ------------ */

    @Transactional
    public LoginResponse login(LoginRequest req, String ip) {

        // Looking for the user
        UserEntity user = userRepo.findByEmailIgnoreCase(req.email())
                .orElseThrow(() -> new BadCredentialsException("Bad credentials"));

        // Check the password + account status
        boolean ok = user.isEnabled() && encoder.matches(req.password(), user.getPassword());

        // Audit (regardless of whether it succeeded or not)
        auditService.record(req.email(), ip, ok);

        if (!ok) {
            throw new BadCredentialsException("Bad credentials");
        }

        // Generate JWTs
        String accessJwt  = jwtService.generateAccessToken(user.getEmail());
        String refreshJwt = jwtService.generateRefreshToken(user.getEmail());

        // Persist refresh-token
        //     ───────────────────────────────────────────────────────────────
        Instant refreshExp = Instant.now().plus(jwtService.getProps().getRefreshTtl());

        refreshService.create(
                user,
                refreshJwt,
                refreshExp
        );

        // Build the answer
        long expiresIn = jwtService.getProps().getAccessTtl().toSeconds();

        return new LoginResponse(accessJwt, refreshJwt, expiresIn);
    }


    /* ------------------------------- REFRESH -------------------------- */

    @Transactional
    public LoginResponse refresh(String refreshToken) {
        var stored = refreshService.findValid(refreshToken);
        if (stored == null) throw new BadCredentialsException("Invalid refresh");

        UserEntity user = stored.getUser();
        String newAccess  = jwtService.generateAccessToken(user.getEmail());
        String newRefresh = jwtService.generateRefreshToken(user.getEmail());

        refreshService.delete(refreshToken);
        refreshService.create(user,newRefresh,
                Instant.now().plusSeconds(jwtService.getProps().getRefreshTtl().toSeconds()));

        // Build the answer
        long expiresIn = jwtService.getProps().getAccessTtl().toSeconds();

        return new LoginResponse(newAccess, newRefresh, expiresIn);
    }

    /* ------------------------------- LOGOUT --------------------------- */

    @Transactional
    public boolean logout(String refreshToken) {
        return refreshService.delete(refreshToken);
    }
}
