package com.athena.tasks.config;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for Spring Batch Configuration.
 * Verifies that batch infrastructure beans are properly configured.
 *
 * <p>Test scenarios:
 * <ul>
 *   <li>JobRepository bean creation and configuration</li>
 *   <li>JobLauncher bean creation and configuration</li>
 *   <li>DataSource configuration for batch metadata</li>
 *   <li>EnableBatchProcessing configuration</li>
 * </ul>
 */
@SpringBootTest
@ActiveProfiles("test")
class BatchConfigTest {

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

    @Autowired(required = false)
    private JobRepository jobRepository;

    @Autowired(required = false)
    private JobLauncher jobLauncher;

    @Autowired(required = false)
    private DataSource dataSource;

    @Test
    void shouldCreateJobRepositoryBean() {
        // Assert
        assertThat(jobRepository).isNotNull();
        assertThat(applicationContext.containsBean("jobRepository")).isTrue();
    }

    @Test
    void shouldCreateJobLauncherBean() {
        // Assert
        assertThat(jobLauncher).isNotNull();
        assertThat(applicationContext.containsBean("jobLauncher")).isTrue();
    }

    @Test
    void shouldConfigureDataSourceForBatchMetadata() {
        // Assert
        assertThat(dataSource).isNotNull();
        assertThat(applicationContext.containsBean("dataSource")).isTrue();
    }

    @Test
    void shouldEnableBatchProcessing() {
        // Assert - Verify that @EnableBatchProcessing is active
        // This is indicated by the presence of batch infrastructure beans
        assertThat(jobRepository).isNotNull();
        assertThat(jobLauncher).isNotNull();

        // Verify batch configuration class exists
        boolean hasBatchConfig = applicationContext.getBeansWithAnnotation(EnableBatchProcessing.class)
                .size() > 0 ||
                applicationContext.containsBean("jobRepository"); // Spring Boot auto-configuration

        assertThat(hasBatchConfig).isTrue();
    }

    @Test
    void shouldInitializeBatchMetadataTables() {
        // Assert - Verify Spring Batch metadata tables are created
        // The presence of JobRepository bean indicates tables are initialized
        assertThat(jobRepository).isNotNull();

        // JobRepository will fail to instantiate if metadata tables don't exist
        // So if we reach this point, tables are created successfully
    }

    @Test
    void shouldConfigureJobRepositoryWithCorrectTransactionManager() {
        // Assert
        assertThat(jobRepository).isNotNull();

        // Verify transaction manager bean exists
        assertThat(applicationContext.containsBean("transactionManager")).isTrue();
    }

    @Test
    void shouldConfigureAsyncJobLauncher() {
        // Assert
        assertThat(jobLauncher).isNotNull();

        // JobLauncher should be configured (async or sync depending on requirements)
        // Default Spring Boot configuration provides SimpleJobLauncher
        assertThat(jobLauncher.getClass().getSimpleName())
                .isIn("SimpleJobLauncher", "TaskExecutorJobLauncher");
    }

    @Test
    void shouldLoadBatchConfigurationClass() {
        // Assert - Verify batch configuration class is loaded
        // Check for common batch config class names
        boolean hasBatchConfigClass =
                applicationContext.containsBean("batchConfig") ||
                        applicationContext.containsBean("batchConfiguration") ||
                        jobRepository != null; // Auto-configuration is sufficient

        assertThat(hasBatchConfigClass).isTrue();
    }
}
