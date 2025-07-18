ALTER TABLE alert.alert_log
    SET (timescaledb.compress, timescaledb.compress_segmentby = 'sensor_id,field');

SELECT add_compression_policy('alert.alert_log', INTERVAL '7 days');

SELECT add_retention_policy('alert.alert_log', INTERVAL '90 days');