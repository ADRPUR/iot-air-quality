SET search_path TO ingest, public;
SELECT add_retention_policy('sensor_data', INTERVAL '30 days');