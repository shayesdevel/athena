package com.athena.tasks.batch;

import com.athena.core.client.AnthropicClaudeClient;
import com.athena.core.entity.Opportunity;
import com.athena.core.entity.OpportunityScore;
import com.athena.core.repository.OpportunityRepository;
import com.athena.core.repository.OpportunityScoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Integration tests for Opportunity Scoring Batch Job.
 * Tests the AI scoring workflow from reading unscored opportunities to persisting scores.
 *
 * <p>Test scenarios:
 * <ul>
 *   <li>Successful scoring of unscored opportunities</li>
 *   <li>Handling of Claude API errors (retry/skip)</li>
 *   <li>Rate limiting compliance (batch size)</li>
 *   <li>Partial completion handling</li>
 *   <li>Score persistence and metadata</li>
 * </ul>
 */
@TestPropertySource(properties = {
        "spring.batch.job.enabled=false", // Prevent auto-execution during tests
        "scoring.batch.size=10" // Override batch size for testing
})
class OpportunityScoringJobTest extends AbstractBatchJobTest {

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private OpportunityScoreRepository opportunityScoreRepository;

    @MockBean
    private AnthropicClaudeClient claudeClient;

    @BeforeEach
    void setUp() {
        // Clean up test data
        opportunityScoreRepository.deleteAll();
        opportunityRepository.deleteAll();
    }

    @Test
    void shouldScoreUnscoredOpportunitiesSuccessfully() throws Exception {
        // Arrange - Create test opportunities without scores
        Opportunity opp1 = createTestOpportunity("TEST-001", "IT Services");
        Opportunity opp2 = createTestOpportunity("TEST-002", "Cloud Infrastructure");
        Opportunity opp3 = createTestOpportunity("TEST-003", "Cybersecurity");

        opportunityRepository.saveAll(List.of(opp1, opp2, opp3));

        // Mock Claude API responses
        when(claudeClient.scoreOpportunity(any(Opportunity.class)))
                .thenReturn(Map.of(
                        "relevance", new BigDecimal("85.5"),
                        "confidence", new BigDecimal("92.0")
                ));

        var parameters = createUniqueJobParameters();

        // Act
        JobExecution execution = runJob(parameters);

        // Assert
        assertJobCompleted(execution);
        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        // Verify all opportunities were scored
        List<OpportunityScore> scores = opportunityScoreRepository.findAll();
        assertThat(scores).hasSize(3);

        // Verify Claude client was called for each opportunity
        verify(claudeClient, times(3)).scoreOpportunity(any(Opportunity.class));

        // Verify scores were persisted correctly
        OpportunityScore firstScore = scores.get(0);
        assertThat(firstScore.getScoreValue()).isEqualByComparingTo(new BigDecimal("85.5"));
        assertThat(firstScore.getConfidence()).isEqualByComparingTo(new BigDecimal("92.0"));
        assertThat(firstScore.getScoredAt()).isNotNull();
    }

    @Test
    void shouldHandleClaudeApiErrors() throws Exception {
        // Arrange
        Opportunity opp1 = createTestOpportunity("TEST-001", "IT Services");
        Opportunity opp2 = createTestOpportunity("TEST-002", "Cloud Infrastructure");
        opportunityRepository.saveAll(List.of(opp1, opp2));

        // Mock API failure for first opportunity, success for second
        when(claudeClient.scoreOpportunity(any(Opportunity.class)))
                .thenThrow(new RuntimeException("Claude API unavailable"))
                .thenReturn(Map.of(
                        "relevance", new BigDecimal("80.0"),
                        "confidence", new BigDecimal("88.0")
                ));

        var parameters = createUniqueJobParameters();

        // Act
        JobExecution execution = runJob(parameters);

        // Assert - Job should complete but with skip count
        assertThat(execution.getStatus()).isIn(BatchStatus.COMPLETED, BatchStatus.COMPLETED_WITH_WARNINGS);

        var stepExecution = execution.getStepExecutions().iterator().next();
        assertThat(stepExecution.getSkipCount()).isGreaterThanOrEqualTo(1);

        // Second opportunity should still be scored
        List<OpportunityScore> scores = opportunityScoreRepository.findAll();
        assertThat(scores).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void shouldRespectBatchSizeForRateLimiting() throws Exception {
        // Arrange - Create more opportunities than batch size
        for (int i = 1; i <= 25; i++) {
            Opportunity opp = createTestOpportunity("TEST-" + String.format("%03d", i), "Test Opportunity " + i);
            opportunityRepository.save(opp);
        }

        when(claudeClient.scoreOpportunity(any(Opportunity.class)))
                .thenReturn(Map.of(
                        "relevance", new BigDecimal("75.0"),
                        "confidence", new BigDecimal("85.0")
                ));

        var parameters = createUniqueJobParameters();

        // Act
        JobExecution execution = runJob(parameters);

        // Assert
        assertJobCompleted(execution);

        var stepExecution = execution.getStepExecutions().iterator().next();
        assertThat(stepExecution.getReadCount()).isEqualTo(25);
        assertThat(stepExecution.getWriteCount()).isEqualTo(25);

        // Verify chunk processing (batch size = 10 from test properties)
        assertThat(stepExecution.getCommitCount()).isGreaterThanOrEqualTo(3); // 25 records / 10 per chunk = 3 commits
    }

    @Test
    void shouldHandlePartialCompletionGracefully() throws Exception {
        // Arrange
        Opportunity opp1 = createTestOpportunity("TEST-001", "IT Services");
        Opportunity opp2 = createTestOpportunity("TEST-002", "Cloud Infrastructure");
        Opportunity opp3 = createTestOpportunity("TEST-003", "Cybersecurity");
        opportunityRepository.saveAll(List.of(opp1, opp2, opp3));

        // Mock: first succeeds, second fails, third succeeds
        when(claudeClient.scoreOpportunity(any(Opportunity.class)))
                .thenReturn(Map.of("relevance", new BigDecimal("80.0"), "confidence", new BigDecimal("90.0")))
                .thenThrow(new RuntimeException("API error"))
                .thenReturn(Map.of("relevance", new BigDecimal("85.0"), "confidence", new BigDecimal("88.0")));

        var parameters = createUniqueJobParameters();

        // Act
        JobExecution execution = runJob(parameters);

        // Assert
        var stepExecution = execution.getStepExecutions().iterator().next();
        assertThat(stepExecution.getReadCount()).isEqualTo(3);
        assertThat(stepExecution.getWriteCount()).isGreaterThanOrEqualTo(2); // At least 2 successful
        assertThat(stepExecution.getSkipCount()).isGreaterThanOrEqualTo(1); // At least 1 failed

        // Verify some scores were persisted
        List<OpportunityScore> scores = opportunityScoreRepository.findAll();
        assertThat(scores).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void shouldOnlyScoreOpportunitiesWithoutExistingScores() throws Exception {
        // Arrange - Create opportunities with mix of scored and unscored
        Opportunity scored = createTestOpportunity("TEST-SCORED", "Already Scored");
        Opportunity unscored1 = createTestOpportunity("TEST-UNSCORED-1", "Not Scored Yet");
        Opportunity unscored2 = createTestOpportunity("TEST-UNSCORED-2", "Also Not Scored");

        opportunityRepository.saveAll(List.of(scored, unscored1, unscored2));

        // Add existing score for first opportunity
        OpportunityScore existingScore = new OpportunityScore(
                scored.getId(),
                "relevance",
                new BigDecimal("90.0")
        );
        opportunityScoreRepository.save(existingScore);

        when(claudeClient.scoreOpportunity(any(Opportunity.class)))
                .thenReturn(Map.of(
                        "relevance", new BigDecimal("75.0"),
                        "confidence", new BigDecimal("85.0")
                ));

        var parameters = createUniqueJobParameters();

        // Act
        JobExecution execution = runJob(parameters);

        // Assert
        assertJobCompleted(execution);

        var stepExecution = execution.getStepExecutions().iterator().next();
        // Should only process unscored opportunities
        assertThat(stepExecution.getReadCount()).isLessThanOrEqualTo(2);

        // Total scores should be 3 (1 existing + 2 new)
        List<OpportunityScore> allScores = opportunityScoreRepository.findAll();
        assertThat(allScores).hasSize(3);
    }

    @Test
    void shouldPersistScoreMetadata() throws Exception {
        // Arrange
        Opportunity opp = createTestOpportunity("TEST-001", "IT Services");
        opportunityRepository.save(opp);

        Map<String, Object> apiResponse = Map.of(
                "relevance", new BigDecimal("85.5"),
                "confidence", new BigDecimal("92.0"),
                "metadata", Map.of(
                        "modelVersion", "claude-3-sonnet",
                        "features", List.of("naics", "keywords", "description")
                )
        );

        when(claudeClient.scoreOpportunity(any(Opportunity.class)))
                .thenReturn(apiResponse);

        var parameters = createUniqueJobParameters();

        // Act
        JobExecution execution = runJob(parameters);

        // Assert
        assertJobCompleted(execution);

        List<OpportunityScore> scores = opportunityScoreRepository.findAll();
        assertThat(scores).hasSize(1);

        OpportunityScore score = scores.get(0);
        assertThat(score.getMetadata()).isNotNull();
        assertThat(score.getMetadata()).containsKey("modelVersion");
    }

    @Test
    void shouldRecordJobExecutionMetrics() throws Exception {
        // Arrange
        Opportunity opp1 = createTestOpportunity("TEST-001", "IT Services");
        Opportunity opp2 = createTestOpportunity("TEST-002", "Cloud Services");
        opportunityRepository.saveAll(List.of(opp1, opp2));

        when(claudeClient.scoreOpportunity(any(Opportunity.class)))
                .thenReturn(Map.of(
                        "relevance", new BigDecimal("80.0"),
                        "confidence", new BigDecimal("90.0")
                ));

        var parameters = createUniqueJobParameters();

        // Act
        JobExecution execution = runJob(parameters);

        // Assert
        assertJobCompleted(execution);

        // Verify job metrics
        assertThat(execution.getJobId()).isNotNull();
        assertThat(execution.getStartTime()).isNotNull();
        assertThat(execution.getEndTime()).isNotNull();
        assertThat(execution.getEndTime()).isAfter(execution.getStartTime());

        var stepExecution = execution.getStepExecutions().iterator().next();
        assertThat(stepExecution.getReadCount()).isEqualTo(2);
        assertThat(stepExecution.getWriteCount()).isEqualTo(2);
        assertThat(stepExecution.getCommitCount()).isGreaterThan(0);
    }

    /**
     * Helper method to create test Opportunity entities.
     */
    private Opportunity createTestOpportunity(String noticeId, String title) {
        Opportunity opp = new Opportunity();
        opp.setNoticeId(noticeId);
        opp.setTitle(title);
        opp.setSolicitationNumber("SOL-" + noticeId);
        opp.setDepartment("Test Department");
        opp.setPostedDate(Instant.now());
        opp.setNaicsCode("541512");
        opp.setActive(true);
        return opp;
    }
}
