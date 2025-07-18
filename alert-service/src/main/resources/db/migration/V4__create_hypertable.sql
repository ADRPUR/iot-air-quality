SELECT create_hypertable(
        'alert.alert_log',
        'created',
        chunk_time_interval => INTERVAL '1 day',
        if_not_exists => TRUE
);
