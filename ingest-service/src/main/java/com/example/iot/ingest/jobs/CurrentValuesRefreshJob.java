package com.example.iot.ingest.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class CurrentValuesRefreshJob {

    private final JdbcTemplate jdbc;
    private final Executor jobExecutor;

    public CurrentValuesRefreshJob(
            JdbcTemplate jdbc,
            @Qualifier("jobExecutor") Executor jobExecutor) {
        this.jdbc = jdbc;
        this.jobExecutor = jobExecutor;
    }

    /**
     * we avoid two simultaneous REFRESH
     */
    private final AtomicBoolean running = new AtomicBoolean(false);

    /**
     * Rebuild the materialized view every 30 seconds,
     * running the SQL asynchronously so as not to block the scheduling thread.
     */
    @Scheduled(initialDelay = 30_000, fixedRate = 30_000)
    public void refresh() {

        if (!running.compareAndSet(false, true)) {
            log.warn("Previous current_values REFRESH still running ➜ skip");
            return;
        }

        CompletableFuture
                .runAsync(() ->
                                jdbc.execute(
                                        "REFRESH MATERIALIZED VIEW CONCURRENTLY ingest.current_values"),
                        jobExecutor)

                .whenComplete((v, ex) -> {
                    running.set(false);
                    if (ex != null) {
                        log.error("REFRESH MATERIALIZED VIEW failed", ex);
                    } else {
                        log.debug("current_values refreshed");
                    }
                });
    }
}
