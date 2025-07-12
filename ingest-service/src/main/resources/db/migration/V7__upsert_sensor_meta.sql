CREATE OR REPLACE FUNCTION ingest.upsert_sensor_meta()
    RETURNS trigger
    LANGUAGE plpgsql AS
$$
BEGIN
    INSERT INTO ingest.sensor_meta (sensor_id)
    VALUES (NEW.sensor_id)
    ON CONFLICT DO NOTHING;
    RETURN NEW;
END;
$$;