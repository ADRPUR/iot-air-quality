SET search_path TO ingest, public;

CREATE MATERIALIZED VIEW IF NOT EXISTS sensor_avg_5m
            WITH (timescaledb.continuous) AS
SELECT time_bucket('5 minutes', time) AS bucket,
       sensor_id,
       field,
       avg(value) AS avg_val
FROM sensor_data
GROUP BY bucket, sensor_id, field
WITH NO DATA;
