-- flyway:transactional=false
-- materialized view with last value per (sensor_id, field)
CREATE MATERIALIZED VIEW IF NOT EXISTS ingest.current_values AS
SELECT DISTINCT ON (sensor_id, field)
    sensor_id,
    field,
    value,
    time AS ts
FROM ingest.sensor_data
ORDER BY sensor_id, field, time DESC
WITH NO DATA;
