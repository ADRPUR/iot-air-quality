package com.example.iot.ingest.jobs;

import com.example.iot.ingest.events.MetricAvgPublisher;
import com.example.iot.ingest.events.MetricAvgUpdate;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class Avg5mRefreshJob {

    private final JdbcTemplate       jdbc;
    private final MetricAvgPublisher publisher;

    private final AtomicBoolean running = new AtomicBoolean(false);

    /**
     * Runs at second 0 of every minute on a thread in the
     * “jobExecutor” pool, so as not to block the scheduler.
     * Finally emits the recalculated <sensorId, field> pairs.
     */
    @Async("jobExecutor")
    @Scheduled(cron = "0 * * * * *")      // second 0, every minute
    @Timed("avg5m_refresh_job")
    public void refresh() {

        /* avoid overlapping executions */
        if (!running.compareAndSet(false, true)) {
            log.warn("avg5mRefresh – previous run still in progress; skip");
            return;
        }

        try {
            /* 1. Timescale CALL (blocking, but on a dedicated thread) */
            jdbc.execute("""
                CALL refresh_continuous_aggregate(
                    'sensor_avg_5m',
                     NOW() - INTERVAL '2 hours',
                     NOW() - INTERVAL '1 minute');
            """);

            /* 2. What aggregates have changed in the last hour? */
            List<MetricAvgUpdate> changed = jdbc.query("""
                SELECT DISTINCT sensor_id, field
                FROM ingest.sensor_avg_5m
                WHERE bucket >= NOW() - INTERVAL '2 hours'
            """, (rs, i) -> new MetricAvgUpdate(rs.getString(1), rs.getString(2)));

            /* 3. Publish to websocket/subscription */
            changed.forEach(publisher::publish);

            log.debug("avg5mRefresh – {} sensor/field pairs updated", changed.size());

        } catch (Exception ex) {
            log.error("avg5mRefresh failed", ex);
        } finally {
            running.set(false);
        }
    }
}
