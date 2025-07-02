# 📑 Database bootstrap for *iot‑air‑quality*

> Run everything with **psql** connected as a super‑user (or a role that can `CREATE EXTENSION`).
> Adjust names/passwords if you changed the defaults in *docker‑compose.yml*.

---

## 1  Prerequisites

```sql
-- PostgreSQL 15+
CREATE EXTENSION IF NOT EXISTS timescaledb;
```

*Verify the version*

```sql
SELECT extversion FROM pg_extension WHERE extname = 'timescaledb';
-- should be 2.13+ (2.14 adds CALL‑style helpers)
```

---

## 2  Base schema

```sql
CREATE SCHEMA IF NOT EXISTS ingest;
SET search_path = ingest, public;
```

### 2.1  Raw measurements table

```sql
CREATE TABLE IF NOT EXISTS sensor_data (
    time       TIMESTAMPTZ       NOT NULL,
    sensor_id  TEXT              NOT NULL,
    field      TEXT              NOT NULL,
    value      DOUBLE PRECISION  NOT NULL,
    id         UUID              DEFAULT gen_random_uuid() PRIMARY KEY
);

-- Convert to hypertable (‑‑ chunked every hour)
SELECT create_hypertable('sensor_data', 'time', chunk_time_interval => INTERVAL '1 hour', if_not_exists => TRUE);
```

---

## 3  Roll‑ups / Continuous Aggregates

### 3.1  5‑minute average per sensor & field

```sql
CREATE MATERIALIZED VIEW IF NOT EXISTS sensor_avg_5m
WITH (timescaledb.continuous) AS
SELECT time_bucket(INTERVAL '5 minutes', time)  AS bucket,
       sensor_id,
       field,
       avg(value)                              AS avg_val
FROM   sensor_data
GROUP  BY bucket, sensor_id, field;
```

### 3.2  Refresh policy (continuous aggregate)

```sql
-- run as SELECT for Timescale ≤ 2.13
SELECT add_continuous_aggregate_policy(
  'sensor_avg_5m',
  start_offset      => INTERVAL '2 hours',   -- how far back to recompute
  end_offset        => INTERVAL '1 minute',  -- ignore very newest data
  schedule_interval => INTERVAL '1 minute'   -- run every minute
);
```

*(If you later upgrade to Timescale 2.14+ you can switch to `CALL` instead of `SELECT`.)*

---

## 4  Retention policy

Keep only the last 30 days of raw data; roll‑ups are preserved.

```sql
SELECT add_retention_policy('sensor_data', INTERVAL '30 days');
```

---

## 5  Inspection helpers

```sql
-- view all background jobs (policy schedulers)
SELECT job_id, hypertable_name, proc_name, schedule_interval, next_start
FROM   timescaledb_information.jobs
ORDER  BY job_id;

-- manual refresh if you need it
CALL refresh_continuous_aggregate('sensor_avg_5m',
      now() - INTERVAL '2 hours',
      now()  - INTERVAL '1 minute');
```

---

## 6  Rollback helpers

```sql
-- drop policies (order matters)
SELECT remove_retention_policy('sensor_data');
SELECT remove_continuous_aggregate_policy('sensor_avg_5m');

-- drop objects
DROP MATERIALIZED VIEW IF EXISTS sensor_avg_5m;
DROP TABLE IF EXISTS sensor_data;
```

---

### ✅  Done

Run these sections in order and the backend is ready for the ingest‑service.
