# Prerequisites (Ubuntu / PostgreSQL 17.5)
To use TimescaleDB on PostgreSQL 17.5, install the packaged extension and preload it:
```bash
sudo apt update
sudo apt install timescaledb-2-postgresql-17
``` 
Then in your `postgresql.conf`, add:
```
shared_preload_libraries = 'timescaledb'
```
and restart the server:
```bash
sudo systemctl restart postgresql
```

1 Instalează și activează TimescaleDB
```sql
CREATE EXTENSION IF NOT EXISTS timescaledb;
SELECT extversion FROM pg_extension WHERE extname = 'timescaledb';
extversion ar trebui ≥ 2.13.

2 Schema de bază

CREATE SCHEMA IF NOT EXISTS ingest;
SET search_path TO ingest, public;

2.1 Tabelul brut sensor_data

CREATE TABLE sensor_data (
    time       TIMESTAMPTZ NOT NULL,
    sensor_id  TEXT        NOT NULL,
    field      TEXT        NOT NULL,
    value      DOUBLE PRECISION NOT NULL,
    id         UUID DEFAULT gen_random_uuid() NOT NULL,

    -- PK COMPUS: time + id  (time e prima coloană → index sortat optim)
    CONSTRAINT sensor_data_pkey PRIMARY KEY (time, id)
);

2.2 Transformă în hypertable

SELECT create_hypertable(
  'sensor_data',
  'time',
  chunk_time_interval => INTERVAL '1 hour',
  if_not_exists       => TRUE
);

3 Continuous aggregate (medie 5 min)

CREATE MATERIALIZED VIEW sensor_avg_5m
WITH (timescaledb.continuous) AS
SELECT
  time_bucket('5 minutes', time) AS bucket,
  sensor_id,
  field,
  avg(value)                     AS avg_val
FROM   sensor_data
GROUP  BY bucket, sensor_id, field;

3.1 Politica de refresh

SELECT add_continuous_aggregate_policy(
  'sensor_avg_5m',
  start_offset      => INTERVAL '2 hours',
  end_offset        => INTERVAL '1 minute',
  schedule_interval => INTERVAL '1 minute'
);

4 Retention policy (30 zile)

SELECT add_retention_policy('sensor_data', INTERVAL '30 days');

5 Verificare rapidă

-- listă job-uri de fond

SELECT job_id, hypertable_name, proc_name, next_start
FROM   timescaledb_information.jobs;

-- test insert

INSERT INTO sensor_data(time, sensor_id, field, value)
VALUES (now(), 'test', 'temperature', 22.3);

-- query continuous aggregate după 1 min

SELECT * FROM sensor_avg_5m ORDER BY bucket DESC LIMIT 5;

6 Rollback (dacă e nevoie)

SELECT remove_retention_policy('sensor_data');
SELECT remove_continuous_aggregate_policy('sensor_avg_5m');

DROP MATERIALIZED VIEW IF EXISTS sensor_avg_5m;
DROP TABLE IF EXISTS sensor_data;
DROP SCHEMA ingest CASCADE;
```
