CREATE TABLE IF NOT EXISTS ingest.sensor_data
(
    time      TIMESTAMPTZ      NOT NULL,
    sensor_id TEXT             NOT NULL,
    field     TEXT             NOT NULL,
    value     DOUBLE PRECISION NOT NULL,
    id        UUID             NOT NULL DEFAULT gen_random_uuid(),
    CONSTRAINT sensor_data_pkey PRIMARY KEY (time, id)
);

SELECT create_hypertable('sensor_data', 'time',
                         chunk_time_interval => INTERVAL '1 hour',
                         if_not_exists => TRUE);