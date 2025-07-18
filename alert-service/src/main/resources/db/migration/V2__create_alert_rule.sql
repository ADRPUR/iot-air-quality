CREATE TABLE alert.alert_rule
(
    id         UUID        PRIMARY KEY,
    sensor_id  VARCHAR(64)  NOT NULL,
    field      VARCHAR(64)  NOT NULL,
    op         VARCHAR(8)   NOT NULL,
    level      VARCHAR(10) NOT NULL,               -- INFO | WARN | CRITICAL
    threshold  DOUBLE PRECISION NOT NULL,
    enabled    BOOLEAN     NOT NULL DEFAULT TRUE,
    created    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated    TIMESTAMPTZ NOT NULL DEFAULT now(),

    CHECK (level IN ('INFO','WARN','CRITICAL'))
);

-- helpful indexes
CREATE INDEX idx_rule_sensor_field
    ON alert.alert_rule (sensor_id, field);

CREATE INDEX idx_rule_enabled
    ON alert.alert_rule (enabled);
