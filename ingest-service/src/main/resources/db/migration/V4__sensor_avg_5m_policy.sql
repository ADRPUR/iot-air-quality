SELECT add_continuous_aggregate_policy(
               'sensor_avg_5m',
               start_offset => INTERVAL '2 hours',
               end_offset   => INTERVAL '1 minute',
               schedule_interval => INTERVAL '1 minute');
