SET search_path TO alert, public;

CREATE TABLE IF NOT EXISTS alert_rule
(
    id      SERIAL PRIMARY KEY,
    field   TEXT             NOT NULL,
    max_val DOUBLE PRECISION NOT NULL,
    enabled BOOLEAN DEFAULT true
);
