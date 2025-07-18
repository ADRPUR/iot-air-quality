package com.example.iot.alert.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executor;

/**
 * Enables @Async.
 * Provides two dedicated pools:
 * • <b>taskScheduler</b> – threads @Scheduled triggers;<br>
 * • <b>jobExecutor</b> – actually runs the CPU / I/O work from jobs,
 * so that the scheduling threads are not blocked.
 */
@Configuration
@EnableAsync
public class SchedulerConfig {

    /** Threads for the Spring Scheduling mechanism (cron, fixedRate, etc.). */
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler sch = new ThreadPoolTaskScheduler();
        sch.setThreadNamePrefix("sched-");
        sch.setPoolSize(2);
        sch.setRemoveOnCancelPolicy(true);
        return sch;
    }

    /**
     * Executor used by the <code>@Async("jobExecutor")</code> annotation
     * – runs blocking logic (JDBC, I/O) without occupying “sched-” threads.
     *
     * <pre>
     * @Async("jobExecutor")
     * public void runHeavyJob() { … }
     * </pre>
     */
    @Bean(name = "jobExecutor")
    public Executor jobExecutor() {
        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setThreadNamePrefix("job-exec-");
        ex.setCorePoolSize(4);
        ex.setMaxPoolSize(8);
        ex.setQueueCapacity(100);
        ex.initialize();
        return ex;
    }
}
