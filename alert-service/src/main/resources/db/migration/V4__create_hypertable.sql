SET search_path TO alert, public;
-- optional: hypertable
SELECT create_hypertable('alert_log', 'ts',
                         if_not_exists => TRUE, chunk_time_interval => INTERVAL '1 day');