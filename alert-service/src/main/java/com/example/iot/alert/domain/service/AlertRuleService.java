package com.example.iot.alert.domain.service;

import com.example.iot.alert.domain.dto.AlertLevel;
import com.example.iot.alert.domain.dto.AlertRuleDto;
import com.example.iot.alert.domain.dto.Operator;
import com.example.iot.alert.domain.mapper.AlertRuleMapper;
import com.example.iot.alert.domain.model.AlertRuleEntity;
import com.example.iot.alert.domain.repo.AlertRuleRepository;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertRuleService {

    private final AlertRuleRepository repo;
    private final AlertRuleMapper     mapper;

    /* =====================================================================
       1) CREATE – called directly from the controller
    ===================================================================== */

    @Transactional
    public AlertRuleDto createRule(String sensorId,
                                   String field,
                                   Operator op,
                                   AlertLevel level,
                                   Double threshold,
                                   boolean enabled) {

        AlertRuleEntity entity = AlertRuleEntity.builder()
                .id(UUID.randomUUID())
                .sensorId(sensorId)
                .field(field)
                .op(op)
                .level(level)
                .threshold(threshold)
                .enabled(enabled)
                .created(Instant.now())
                .updated(Instant.now())
                .build();

        validate(entity);
        AlertRuleEntity saved = repo.save(entity);
        log.info("Rule created {}", saved.getId());

        return mapper.toDto(saved);
    }

    /* =====================================================================
       2) UPDATE – patch only the fields provided
    ===================================================================== */

    @Transactional
    public AlertRuleDto updateRule(UUID id,
                                   Operator op,
                                   AlertLevel level,
                                   Double threshold,
                                   Boolean enabled) {

        AlertRuleEntity entity = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rule not found: " + id));

        if (op        != null) entity.setOp(op);
        if (threshold != null) entity.setThreshold(threshold);
        if (enabled   != null) entity.setEnabled(enabled);
        if (level   != null) entity.setLevel(level);

        entity.setUpdated(Instant.now());

        validate(entity);
        return mapper.toDto(entity);      // entity is “dirty” → Hibernate saves it on commit
    }

    /* =====================================================================
       3) DELETE
    ===================================================================== */

    @Transactional
    public boolean deleteRule(UUID id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    /* =====================================================================
       4) INTERROGATIONS (unmodified)
    ===================================================================== */

    public List<AlertRuleDto> findAll()               { return mapper.toDto(repo.findAll()); }
    public List<AlertRuleDto> findActive()            { return mapper.toDto(repo.findByEnabledTrue()); }
    public List<AlertRuleDto> findBySensorField(String sensor, String field) {
        return mapper.toDto(repo.findBySensorIdAndField(sensor, field));
    }
    public Page<AlertRuleDto> findPage(int p, int s)  {
        return repo.findAll(PageRequest.of(p, s)).map(mapper::toDto);
    }

    /* =====================================================================
       Helpers
    ===================================================================== */

    private void validate(AlertRuleEntity e) {
        if (e.getThreshold() == null) {
            throw new ConstraintViolationException("Threshold can't be null", null);
        }
    }

    public List<AlertRuleDto> findRules(String  sensorId,
                                        String  field,
                                        Boolean enabled) {

        Specification<AlertRuleEntity> spec = Specification.where(null);

        if (sensorId != null) {
            spec = spec.and((r, q, b) -> b.equal(r.get("sensorId"), sensorId));
        }
        if (field != null) {
            spec = spec.and((r, q, b) -> b.equal(r.get("field"), field));
        }
        if (enabled != null) {
            spec = spec.and((r, q, b) -> b.equal(r.get("enabled"), enabled));
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "created")
                .and(Sort.by("sensorId").ascending())
                .and(Sort.by("field").ascending());

        return mapper.toDto(repo.findAll(spec, sort));
    }

}
