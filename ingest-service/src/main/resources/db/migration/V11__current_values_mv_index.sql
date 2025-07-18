-- flyway:transactional=false
CREATE UNIQUE INDEX IF NOT EXISTS current_values_pk
    ON ingest.current_values (sensor_id, field);