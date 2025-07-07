SET search_path TO alert, public;
SELECT public.create_hypertable(
  'alert.alert_log'::regclass,
  'ts'::name,
  chunk_time_interval => INTERVAL '1 day',
  if_not_exists       => TRUE
);
