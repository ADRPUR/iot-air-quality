-- =======================================================================
-- Create table for alert history
-- =======================================================================

CREATE TABLE IF NOT EXISTS alert.alert_log
(
    id         BIGSERIAL NOT NULL,
    created    TIMESTAMPTZ NOT NULL DEFAULT now(),
    sensor_id  TEXT        NOT NULL,
    field      TEXT        NOT NULL,
    rule_code  TEXT        NOT NULL,
    level      VARCHAR(10) NOT NULL,               -- INFO | WARN | CRITICAL
    message    TEXT        NOT NULL,
    ack        BOOLEAN     NOT NULL DEFAULT FALSE,
    ack_time   TIMESTAMPTZ,

    CHECK (level IN ('INFO','WARN','CRITICAL')),
    PRIMARY KEY (id, created)
);

-- optimized indexes for frequent filters
CREATE INDEX IF NOT EXISTS idx_alert_created
    ON alert.alert_log (created DESC);

CREATE INDEX IF NOT EXISTS idx_alert_sensor_field
    ON alert.alert_log (sensor_id, field);

CREATE INDEX IF NOT EXISTS idx_alert_ack
    ON alert.alert_log (ack);
