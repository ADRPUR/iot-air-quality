package com.example.iot.authservice.domain.service;

import com.example.iot.authservice.domain.dto.LoginAuditDto;
import com.example.iot.authservice.domain.mapper.LoginAuditMapper;
import com.example.iot.authservice.domain.model.LoginAuditEntity;
import com.example.iot.authservice.domain.model.UserEntity;
import com.example.iot.authservice.domain.repo.LoginAuditRepository;
import com.example.iot.authservice.domain.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginAuditService {

    private final LoginAuditRepository repo;
    private final UserRepository userRepo;
    private final LoginAuditMapper mapper;

    @Transactional
    public void record(String email, String ip, boolean success) {

        UserEntity user = userRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found: " + email));

        LoginAuditEntity e = new LoginAuditEntity();
        e.setTs(Instant.now());
        e.setUser(user);
        e.setIp(ip);
        e.setSuccess(success);

        repo.save(e);
    }

    public List<LoginAuditDto> last20(UUID userId) {
        return mapper.toDto(repo.findTop20ByUserIdOrderByTsDesc(userId));
    }
}
