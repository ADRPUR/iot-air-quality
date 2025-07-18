package com.example.iot.alert.domain.model;

import com.example.iot.alert.domain.dto.AlertLevel;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * JPA entity mapped to the <schema>.alert_log table.
 */
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "alert_log", schema = "alert",
        indexes = {
                @Index(name = "idx_alert_created",  columnList = "created"),
                @Index(name = "idx_alert_sensor_field", columnList = "sensor_id,field"),
                @Index(name = "idx_alert_ack",      columnList = "ack")
        })
public class AlertLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Trigger time (UTC). Set from the application, but also default in the DB. */
    @Column(nullable = false, columnDefinition = "TIMESTAMPTZ")
    private Instant created;

    @Column(name = "sensor_id", nullable = false)
    private String sensorId;

    @Column(nullable = false)
    private String field;

    @Column(name = "rule_code", nullable = false)
    private String ruleCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AlertLevel level;

    @Column(nullable = false)
    private String message;

    /** Did the operator acknowledge the alert? */
    @Column(nullable = false)
    private Boolean ack = Boolean.FALSE;

    /** When  acknowledged. */
    @Column(name = "ack_time", columnDefinition = "TIMESTAMPTZ")
    private Instant ackTime;
}
