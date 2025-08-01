SELECT create_hypertable(
               'auth.login_audit',
               'ts',
               chunk_time_interval => INTERVAL '1 day',
               if_not_exists => TRUE
       );
