package com.athena.tasks.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuration for scheduled tasks (@Scheduled).
 *
 * Enables:
 * - @EnableScheduling for cron-based task execution
 * - @EnableAsync for asynchronous task processing
 * - Thread pool for concurrent task execution
 *
 * Scheduled tasks:
 * - High-score alert job (8 AM weekdays)
 * - Weekly digest job (9 AM Mondays)
 */
@Configuration
@EnableScheduling
@EnableAsync
public class SchedulingConfig {

    /**
     * Configure thread pool for async and scheduled task execution.
     *
     * Configuration:
     * - Core pool size: 5 threads (handles concurrent scheduled tasks)
     * - Max pool size: 10 threads (peak capacity)
     * - Queue capacity: 25 tasks (buffered work)
     * - Thread name prefix: "athena-task-"
     *
     * @return Configured task executor
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("athena-task-");
        executor.initialize();
        return executor;
    }
}
