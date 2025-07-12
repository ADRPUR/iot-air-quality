package com.example.iot.ingest.jobs;

import com.example.iot.ingest.events.MetricAvgPublisher;
import com.example.iot.ingest.events.MetricAvgUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class Avg5mRefreshJob {

    private final JdbcTemplate       jdbc;
    private final MetricAvgPublisher publisher;

    @Scheduled(cron = "0 * * * * *")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void run() {

        jdbc.update("""
            CALL refresh_continuous_aggregate(
                'sensor_avg_5m',
                 NOW() - INTERVAL '2 hours',
                 NOW() - INTERVAL '1 minute');
        """);
        log.info("Continuous-aggregate refresh OK");

        var rows = jdbc.query("""
            SELECT DISTINCT sensor_id, field
            FROM ingest.sensor_avg_5m
            WHERE bucket >= NOW() - INTERVAL '2 hours'
        """, (rs, i) -> new MetricAvgUpdate(
                rs.getString("sensor_id"),
                rs.getString("field"))
        );

        rows.forEach(u -> publisher.publish(u.sensorId(), u.field()));
        log.info("Pushed {} update(s) to subscription", rows.size());
    }
}