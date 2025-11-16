package com.athena.tasks.batch;

import org.junit.jupiter.api.AfterEach;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Abstract base class for Spring Batch job integration tests.
 * Extends AbstractIntegrationTest to provide Testcontainers PostgreSQL support
 * and adds Spring Batch testing utilities.
 *
 * <p>Tests extending this class automatically have access to:
 * <ul>
 *   <li>PostgreSQL database running in Docker via Testcontainers</li>
 *   <li>JobLauncherTestUtils for running jobs synchronously</li>
 *   <li>JobRepositoryTestUtils for cleaning up job metadata</li>
 *   <li>Helper methods for common batch testing operations</li>
 * </ul>
 *
 * <p>The PostgreSQL container is shared across all tests for performance.</p>
 *
 * <p>Usage example:
 * <pre>
 * {@code
 * class MyJobTest extends AbstractBatchJobTest {
 *
 *     @Test
 *     void shouldProcessRecords() {
 *         JobExecution execution = runJob();
 *         assertJobCompleted(execution);
 *         // Additional assertions...
 *     }
 * }
 * }
 * </pre>
 */
@SpringBatchTest
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public abstract class AbstractBatchJobTest {

    /**
     * Singleton PostgreSQL container instance shared across all tests.
     * Uses postgres:17-alpine for consistency with production environment.
     */
    private static final PostgreSQLContainer<?> postgresContainer;

    static {
        postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:17-alpine"))
                .withDatabaseName("athena_test")
                .withUsername("test")
                .withPassword("test")
                .withReuse(true);
        postgresContainer.start();
    }

    @Autowired
    protected JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    protected JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    protected JobRepository jobRepository;

    /**
     * Configures Spring Boot test properties to use the Testcontainers database.
     * This method is called by Spring's test framework before the ApplicationContext loads.
     *
     * @param registry the dynamic property registry to add properties to
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    /**
     * Cleans up Spring Batch job metadata after each test.
     * Ensures test isolation by removing job execution history.
     */
    @AfterEach
    void cleanupJobMetadata() {
        if (jobRepositoryTestUtils != null) {
            jobRepositoryTestUtils.removeJobExecutions();
        }
    }

    /**
     * Runs the job under test with default (empty) job parameters.
     *
     * @return the JobExecution representing the completed job run
     * @throws Exception if job execution fails
     */
    protected JobExecution runJob() throws Exception {
        return jobLauncherTestUtils.launchJob();
    }

    /**
     * Runs the job under test with specified job parameters.
     *
     * @param parameters the job parameters to use
     * @return the JobExecution representing the completed job run
     * @throws Exception if job execution fails
     */
    protected JobExecution runJob(JobParameters parameters) throws Exception {
        return jobLauncherTestUtils.launchJob(parameters);
    }

    /**
     * Runs a specific step within the job under test.
     *
     * @param stepName the name of the step to run
     * @return the JobExecution representing the completed step run
     */
    protected JobExecution runStep(String stepName) {
        return jobLauncherTestUtils.launchStep(stepName);
    }

    /**
     * Returns the job under test (configured by JobLauncherTestUtils).
     *
     * @return the Job bean
     */
    protected Job getJob() {
        return jobLauncherTestUtils.getJob();
    }

    /**
     * Asserts that a job execution completed successfully.
     *
     * @param execution the job execution to check
     */
    protected void assertJobCompleted(JobExecution execution) {
        assertThat(execution).isNotNull();
        assertThat(execution.getStatus().isUnsuccessful()).isFalse();
        assertThat(execution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");
    }

    /**
     * Asserts that a job execution failed.
     *
     * @param execution the job execution to check
     */
    protected void assertJobFailed(JobExecution execution) {
        assertThat(execution).isNotNull();
        assertThat(execution.getStatus().isUnsuccessful()).isTrue();
    }

    /**
     * Creates a JobParameters instance with a unique run ID to prevent
     * "A job instance already exists" errors during testing.
     *
     * @return new JobParameters with unique run.id
     */
    protected JobParameters createUniqueJobParameters() {
        return new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();
    }

    /**
     * Creates a JobParameters instance with a unique run ID and additional parameters.
     *
     * @param builder the JobParametersBuilder with additional parameters
     * @return JobParameters with unique run.id and additional parameters
     */
    protected JobParameters createUniqueJobParameters(JobParametersBuilder builder) {
        return builder
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();
    }
}
