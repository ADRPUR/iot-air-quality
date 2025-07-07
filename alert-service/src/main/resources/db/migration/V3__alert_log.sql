SET search_path TO alert, public;

CREATE TABLE IF NOT EXISTS alert_log
(
    id        UUID NOT NULL DEFAULT gen_random_uuid(),
    ts        TIMESTAMPTZ      NOT NULL DEFAULT now(),
    sensor_id TEXT             NOT NULL,
    field     TEXT             NOT NULL,
    value     DOUBLE PRECISION NOT NULL,
    "limit"   DOUBLE PRECISION NOT NULL,
    channels  TEXT[]           NOT NULL,
    PRIMARY KEY (id, ts)
);
