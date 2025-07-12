CREATE EXTENSION IF NOT EXISTS timescaledb WITH SCHEMA public;
CREATE SCHEMA IF NOT EXISTS alert;
SET search_path = alert, public, timescaledb;
