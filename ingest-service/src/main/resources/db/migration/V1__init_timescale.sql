CREATE EXTENSION IF NOT EXISTS timescaledb WITH SCHEMA public;
CREATE SCHEMA IF NOT EXISTS ingest;
SET search_path = ingest, alert, public, timescaledb;