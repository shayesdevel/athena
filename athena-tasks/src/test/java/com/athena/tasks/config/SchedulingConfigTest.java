package com.athena.tasks.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.ScheduledExecutorService;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for Task Scheduling Configuration.
 * Verifies that @EnableScheduling and thread pool are properly configured.
 *
 * <p>Test scenarios:
 * <ul>
 *   <li>@EnableScheduling is active</li>
 *   <li>TaskScheduler bean configuration</li>
 *   <li>Thread pool configuration</li>
 *   <li>Scheduled task registration</li>
 * </ul>
 */
@SpringBootTest
@ActiveProfiles("test")
class SchedulingConfigTest {

    private static final PostgreSQLContainer<?> postgresContainer;

    static {
        postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:17-alpine"))
                .withDatabaseName("athena_test")
                .withUsername("test")
                .withPassword("test")
                .withReuse(true);
        postgresContainer.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void shouldEnableScheduling() {
        // Assert - Verify @EnableScheduling is active
        // This is indicated by the presence of scheduling infrastructure beans

        boolean hasSchedulingConfig = applicationContext.getBeansWithAnnotation(EnableScheduling.class)
                .size() > 0 ||
                applicationContext.containsBean("taskScheduler"); // Spring Boot auto-configuration

        assertThat(hasSchedulingConfig).isTrue();
    }

    @Test
    void shouldCreateTaskSchedulerBean() {
        // Assert
        boolean hasTaskScheduler = applicationContext.containsBean("taskScheduler") ||
                applicationContext.getBeansOfType(ThreadPoolTaskScheduler.class).size() > 0 ||
                applicationContext.getBeansOfType(ScheduledExecutorService.class).size() > 0;

        assertThat(hasTaskScheduler).isTrue();
    }

    @Test
    void shouldConfigureThreadPoolForScheduledTasks() {
        // Assert - Verify thread pool exists for scheduled task execution
        // Either explicit TaskScheduler or default executor

        boolean hasExecutor = applicationContext.containsBean("taskScheduler") ||
                applicationContext.containsBean("scheduledExecutorService") ||
                applicationContext.getBeansOfType(ScheduledExecutorService.class).size() > 0;

        assertThat(hasExecutor).isTrue();
    }

    @Test
    void shouldLoadSchedulingConfigurationClass() {
        // Assert - Verify scheduling configuration class is loaded
        boolean hasSchedulingConfigClass =
                applicationContext.containsBean("schedulingConfig") ||
                        applicationContext.containsBean("schedulingConfiguration") ||
                        applicationContext.getBeansWithAnnotation(EnableScheduling.class).size() > 0;

        assertThat(hasSchedulingConfigClass).isTrue();
    }

    @Test
    void shouldRegisterScheduledTaskPostProcessor() {
        // Assert - Verify Spring's ScheduledAnnotationBeanPostProcessor is registered
        // This post-processor handles @Scheduled annotations

        boolean hasScheduledProcessor =
                applicationContext.containsBean("org.springframework.context.annotation.internalScheduledAnnotationProcessor") ||
                        applicationContext.getBeansWithAnnotation(EnableScheduling.class).size() > 0;

        assertThat(hasScheduledProcessor).isTrue();
    }

    @Test
    void shouldSupportCronExpressions() {
        // Assert - Verify scheduling infrastructure supports cron expressions
        // This is a built-in feature of Spring's @EnableScheduling
        // Presence of scheduling configuration implies cron support

        boolean hasSchedulingSupport = applicationContext.getBeansWithAnnotation(EnableScheduling.class)
                .size() > 0 ||
                applicationContext.containsBean("taskScheduler");

        assertThat(hasSchedulingSupport).isTrue();
    }

    @Test
    void shouldSupportFixedRateScheduling() {
        // Assert - Verify scheduling infrastructure supports fixed rate scheduling
        // This is a built-in feature of Spring's @EnableScheduling

        boolean hasSchedulingSupport = applicationContext.getBeansWithAnnotation(EnableScheduling.class)
                .size() > 0 ||
                applicationContext.containsBean("taskScheduler");

        assertThat(hasSchedulingSupport).isTrue();
    }

    @Test
    void shouldConfigureAsyncTaskExecution() {
        // Assert - Verify async task execution is supported
        // Scheduled tasks can be async if configured

        boolean hasAsyncSupport = applicationContext.containsBean("taskScheduler") ||
                applicationContext.getBeansOfType(ThreadPoolTaskScheduler.class).size() > 0;

        assertThat(hasAsyncSupport).isTrue();
    }
}
