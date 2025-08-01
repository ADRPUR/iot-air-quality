package com.example.iot.authservice.domain.service;

import com.example.iot.authservice.domain.model.RefreshTokenEntity;
import com.example.iot.authservice.domain.model.UserEntity;
import com.example.iot.authservice.domain.repo.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repo;

    @Transactional
    public RefreshTokenEntity create(UserEntity user, String token, Instant expiry) {
        RefreshTokenEntity ent = new RefreshTokenEntity();
        ent.setToken(token);
        ent.setUser(user);
        ent.setExpiry(expiry);
        return repo.save(ent);
    }

    public RefreshTokenEntity findValid(String token) {
        return repo.findByToken(token)
                .filter(t -> t.getExpiry().isAfter(Instant.now()))
                .orElse(null);
    }

    @Transactional
    public boolean delete(String token) {
        if (token == null || token.isBlank()) {
                return false;
        }
        Optional<RefreshTokenEntity> optional = repo.findByToken(token);
        if (optional.isPresent()) {
            repo.deleteByToken(token);
            return true;
        }
        return false;
    }
}
