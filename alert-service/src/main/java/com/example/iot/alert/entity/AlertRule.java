package com.example.iot.alert.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "alert_rule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertRule {

    /**
     * Maps to the SERIAL primary key in the DB
     * (uses IDENTITY strategy for PostgreSQL SERIAL).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The field being monitored (maps to column `field`)
     */
    @Column(name = "field", nullable = false)
    private String fieldName;

    /**
     * The maximum value threshold (maps to column `max_val`)
     */
    @Column(name = "max_val", nullable = false)
    private double maxVal;

    /**
     * Whether this rule is active
     */
    @Column(nullable = false)
    private boolean enabled;

    // add GraphQL field alias for 'field'
    public String getField() {
        return this.fieldName;
    }
}
