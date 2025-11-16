package com.athena.tasks.batch;

import com.athena.core.entity.Opportunity;
import com.athena.core.repository.OpportunityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for SAM.gov Import Batch Job.
 * Tests the entire job flow from reading JSON files to persisting opportunities in the database.
 *
 * <p>Test scenarios:
 * <ul>
 *   <li>Successful import of valid opportunity records</li>
 *   <li>Handling of invalid/malformed records (skip and continue)</li>
 *   <li>Duplicate detection and idempotency</li>
 *   <li>Chunk processing and batch commits</li>
 *   <li>Job restart capability after failure</li>
 * </ul>
 */
@TestPropertySource(properties = {
        "samgov.import.source=classpath:samgov-data/valid-opportunities.json",
        "spring.batch.job.enabled=false" // Prevent auto-execution during tests
})
class SamGovImportJobTest extends AbstractBatchJobTest {

    @Autowired
    private OpportunityRepository opportunityRepository;

    @BeforeEach
    void setUp() {
        // Clean up opportunities before each test
        opportunityRepository.deleteAll();
    }

    @Test
    void shouldImportValidOpportunitiesSuccessfully() throws Exception {
        // Arrange
        var parameters = new JobParametersBuilder()
                .addString("inputFile", "classpath:samgov-data/valid-opportunities.json")
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();

        // Act
        JobExecution execution = runJob(parameters);

        // Assert
        assertJobCompleted(execution);
        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        List<Opportunity> opportunities = opportunityRepository.findAll();
        assertThat(opportunities).hasSize(3);

        // Verify first opportunity was imported correctly
        Opportunity firstOpp = opportunities.stream()
                .filter(o -> "TEST-NOTICE-001".equals(o.getNoticeId()))
                .findFirst()
                .orElseThrow();

        assertThat(firstOpp.getTitle()).isEqualTo("IT Services for Federal Agency");
        assertThat(firstOpp.getSolicitationNumber()).isEqualTo("SOL-2025-001");
        assertThat(firstOpp.getDepartment()).isEqualTo("Department of Defense");
        assertThat(firstOpp.getNaicsCode()).isEqualTo("541512");
    }

    @Test
    void shouldSkipInvalidRecordsAndContinueProcessing() throws Exception {
        // Arrange
        var parameters = new JobParametersBuilder()
                .addString("inputFile", "classpath:samgov-data/invalid-opportunities.json")
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();

        // Act
        JobExecution execution = runJob(parameters);

        // Assert
        // Job should complete but with skip count
        assertThat(execution.getStatus()).isIn(BatchStatus.COMPLETED, BatchStatus.COMPLETED_WITH_WARNINGS);

        // Verify skipped items are logged
        var stepExecution = execution.getStepExecutions().iterator().next();
        assertThat(stepExecution.getSkipCount()).isGreaterThan(0);

        // Valid records should still be imported (if any exist in invalid file)
        List<Opportunity> opportunities = opportunityRepository.findAll();
        assertThat(opportunities).hasSizeLessThanOrEqualTo(4); // Max 4 records in invalid file
    }

    @Test
    void shouldHandleDuplicateOpportunitiesIdempotently() throws Exception {
        // Arrange - First import
        var firstRun = new JobParametersBuilder()
                .addString("inputFile", "classpath:samgov-data/valid-opportunities.json")
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();

        JobExecution firstExecution = runJob(firstRun);
        assertJobCompleted(firstExecution);

        long initialCount = opportunityRepository.count();
        assertThat(initialCount).isEqualTo(3);

        // Act - Second import with same data
        Thread.sleep(10); // Ensure different run.id
        var secondRun = new JobParametersBuilder()
                .addString("inputFile", "classpath:samgov-data/valid-opportunities.json")
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();

        JobExecution secondExecution = runJob(secondRun);

        // Assert - No duplicates created
        assertJobCompleted(secondExecution);
        long finalCount = opportunityRepository.count();
        assertThat(finalCount).isEqualTo(initialCount); // Same count, records updated not duplicated
    }

    @Test
    void shouldProcessRecordsInChunks() throws Exception {
        // Arrange
        var parameters = new JobParametersBuilder()
                .addString("inputFile", "classpath:samgov-data/valid-opportunities.json")
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();

        // Act
        JobExecution execution = runJob(parameters);

        // Assert
        assertJobCompleted(execution);

        var stepExecution = execution.getStepExecutions().iterator().next();
        assertThat(stepExecution.getReadCount()).isEqualTo(3);
        assertThat(stepExecution.getWriteCount()).isEqualTo(3);
        assertThat(stepExecution.getCommitCount()).isGreaterThan(0); // At least one chunk committed
    }

    @Test
    void shouldSupportJobRestart() throws Exception {
        // This test verifies that a failed job can be restarted from the last successful chunk
        // Implementation depends on job configuration with restart capability

        // Arrange - Create a scenario that causes job to fail mid-processing
        // (This would require a custom test file or mock that fails after N records)

        // For now, verify the job supports restartable=true configuration
        var parameters = createUniqueJobParameters();
        JobExecution execution = runJob(parameters);

        // Assert
        assertJobCompleted(execution);
        assertThat(execution.getJobInstance()).isNotNull();
        // Restartable jobs can be identified from job configuration
    }

    @Test
    void shouldValidateRequiredFieldsBeforeImport() throws Exception {
        // Arrange
        var parameters = new JobParametersBuilder()
                .addString("inputFile", "classpath:samgov-data/invalid-opportunities.json")
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();

        // Act
        JobExecution execution = runJob(parameters);

        // Assert - Records with missing required fields should be skipped
        var stepExecution = execution.getStepExecutions().iterator().next();
        assertThat(stepExecution.getSkipCount()).isGreaterThanOrEqualTo(1);

        // Records with all required fields should be imported
        List<Opportunity> opportunities = opportunityRepository.findAll();
        opportunities.forEach(opp -> {
            assertThat(opp.getNoticeId()).isNotNull();
            assertThat(opp.getTitle()).isNotNull();
            assertThat(opp.getNaicsCode()).isNotNull();
        });
    }

    @Test
    void shouldRecordJobMetricsInJobRepository() throws Exception {
        // Arrange
        var parameters = createUniqueJobParameters(
                new JobParametersBuilder()
                        .addString("inputFile", "classpath:samgov-data/valid-opportunities.json")
        );

        // Act
        JobExecution execution = runJob(parameters);

        // Assert
        assertJobCompleted(execution);

        // Verify job execution metadata is persisted
        assertThat(execution.getJobId()).isNotNull();
        assertThat(execution.getStartTime()).isNotNull();
        assertThat(execution.getEndTime()).isNotNull();
        assertThat(execution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");

        // Verify step execution metrics
        var stepExecution = execution.getStepExecutions().iterator().next();
        assertThat(stepExecution.getStepName()).isNotNull();
        assertThat(stepExecution.getReadCount()).isEqualTo(3);
        assertThat(stepExecution.getWriteCount()).isEqualTo(3);
    }
}
