SELECT create_hypertable(
  'alert_log'::regclass,
  'ts'::name,
  chunk_time_interval => INTERVAL '1 day',
  if_not_exists       => TRUE
);
