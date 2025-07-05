SET search_path TO alert, public;

INSERT INTO alert_rule (field, max_val)
VALUES ('temperature', 30),
       ('humidity', 80),
       ('pm25', 50),
       ('air_quality', 150)
ON CONFLICT DO NOTHING;
