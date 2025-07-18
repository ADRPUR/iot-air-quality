package com.example.iot.alert.domain.model;

import com.example.iot.alert.domain.dto.AlertLevel;
import com.example.iot.alert.domain.dto.Operator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * Persistent definition of an alert rule.
 *
 * Example rule:
 *   - sensorId = "bme280_1"
 *   - field    = "temperature"
 *   - op       = GT
 *   - threshold= 30 °C
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "alert_rule",
        indexes = {
                @Index(name = "idx_rule_sensor_field", columnList = "sensor_id,field"),
                @Index(name = "idx_rule_enabled",      columnList = "enabled")
        })
public class AlertRuleEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "sensor_id", nullable = false, length = 64)
    private String sensorId;

    @Column(nullable = false, length = 64)
    private String field;

    /** Comparison operator (LT, LTE, EQ, GTE, GT, CHANGE …). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 8)
    private Operator op;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AlertLevel level;

    /** Threshold value (°C, %, ppm …) interpreted together with {@code op}. */
    @Column(nullable = false)
    private Double threshold;

    /** Rule active/inactive. */
    @Column(nullable = false)
    private boolean enabled = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant created;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updated;
}

