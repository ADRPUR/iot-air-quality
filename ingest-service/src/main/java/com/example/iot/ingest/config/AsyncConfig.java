package com.example.iot.ingest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Pool dedicated to scheduled jobs that may take longer
 * (view refreshes, 5m aggregations, etc.). We do not block the thread
 * by default <scheduling-1>.
 */
@EnableAsync
@Configuration
public class AsyncConfig {

    @Bean(name = "jobExecutor")
    public Executor jobExecutor() {
        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setThreadNamePrefix("jobs-");
        ex.setCorePoolSize(2);     // ↯ adjusts by number of jobs
        ex.setMaxPoolSize(4);
        ex.setQueueCapacity(20);
        ex.initialize();
        return ex;
    }
}
