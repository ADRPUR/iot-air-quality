-- V3__sensor_data_indexes.sql  (Flyway)
CREATE INDEX IF NOT EXISTS
    sensor_data_sensor_field_time_idx
    ON ingest.sensor_data (sensor_id, field, "time" DESC);