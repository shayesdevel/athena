package com.athena.tasks.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Spring Batch configuration for Athena background jobs.
 *
 * Configures:
 * - JobRepository (stores job execution metadata)
 * - JobLauncher (executes batch jobs)
 * - Transaction management
 *
 * Jobs defined in this configuration:
 * - SAM.gov data import (load opportunities from JSON files)
 * - AI opportunity scoring (score unscored opportunities)
 */
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    /**
     * Configure async job launcher for background job execution.
     * Uses SimpleAsyncTaskExecutor to run jobs asynchronously.
     *
     * @param jobRepository Spring Batch job repository
     * @return Configured JobLauncher
     */
    @Bean
    public JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }
}
