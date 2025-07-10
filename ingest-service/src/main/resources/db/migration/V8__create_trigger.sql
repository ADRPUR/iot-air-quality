CREATE TRIGGER trg_upsert_sensor_meta
    AFTER INSERT OR UPDATE OF sensor_id ON ingest.sensor_data
    FOR EACH ROW EXECUTE FUNCTION ingest.upsert_sensor_meta();