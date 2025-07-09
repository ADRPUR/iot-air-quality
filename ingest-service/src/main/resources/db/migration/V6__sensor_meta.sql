-- 📦  ingest.sensor_meta  (meta-data per physical sensor)
CREATE TABLE IF NOT EXISTS sensor_meta
(
    sensor_id TEXT PRIMARY KEY,
    name      TEXT,
    visible   BOOLEAN NOT NULL DEFAULT TRUE
);

-- populate with distinct ids already seen în sensor_data
INSERT INTO sensor_meta (sensor_id)
SELECT DISTINCT sensor_id
FROM ingest.sensor_data
ON CONFLICT DO NOTHING;