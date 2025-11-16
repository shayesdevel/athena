package com.athena.tasks.batch;

import com.athena.core.client.AnthropicClaudeClient;
import com.athena.core.entity.Opportunity;
import com.athena.core.entity.OpportunityScore;
import com.athena.core.repository.OpportunityRepository;
import com.athena.core.repository.OpportunityScoreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;
import java.util.Map;

/**
 * Spring Batch job for AI scoring of opportunities using Claude API.
 *
 * Job: opportunityScoringJob
 * Step: scoreOpportunitiesStep
 *
 * Processing flow:
 * 1. Reader: Find opportunities without AI scores (where no OpportunityScore exists with scoreType='AI')
 * 2. Processor: Call Claude API to score opportunity (via AnthropicClaudeClient)
 * 3. Writer: Save OpportunityScore entities
 *
 * Configuration:
 * - Chunk size: 10 (respects API rate limits, batch of 10 opportunities at a time)
 * - Skip policy: Skip individual API failures, continue processing
 * - Retry: 3 attempts per opportunity (handles transient API errors)
 *
 * Error Handling:
 * - API failures logged, opportunity skipped
 * - Malformed responses logged, opportunity skipped
 * - Max retries: 3 per opportunity
 */
@Configuration
public class OpportunityScoringJob {

    private static final Logger logger = LoggerFactory.getLogger(OpportunityScoringJob.class);

    private final OpportunityRepository opportunityRepository;
    private final OpportunityScoreRepository scoreRepository;
    private final AnthropicClaudeClient claudeClient;

    @Value("${athena.scoring.company-capabilities:Government contracting experience with cloud infrastructure, cybersecurity, and data analytics}")
    private String companyCapabilities;

    public OpportunityScoringJob(
            OpportunityRepository opportunityRepository,
            OpportunityScoreRepository scoreRepository,
            AnthropicClaudeClient claudeClient) {
        this.opportunityRepository = opportunityRepository;
        this.scoreRepository = scoreRepository;
        this.claudeClient = claudeClient;
    }

    /**
     * Define the opportunity scoring job.
     *
     * @param jobRepository Spring Batch job repository
     * @param scoreOpportunitiesStep Step for scoring opportunities
     * @return Configured job
     */
    @Bean
    public Job opportunityScoringJob(
            JobRepository jobRepository,
            Step scoreOpportunitiesStep) {
        return new JobBuilder("opportunityScoringJob", jobRepository)
                .start(scoreOpportunitiesStep)
                .build();
    }

    /**
     * Define the score opportunities step.
     *
     * @param jobRepository Spring Batch job repository
     * @param transactionManager Transaction manager
     * @return Configured step
     */
    @Bean
    public Step scoreOpportunitiesStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager) {
        return new StepBuilder("scoreOpportunitiesStep", jobRepository)
                .<Opportunity, OpportunityScore>chunk(10, transactionManager)
                .reader(unscoredOpportunityReader())
                .processor(scoreProcessor())
                .writer(scoreWriter())
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(Integer.MAX_VALUE)
                .retryLimit(3)
                .retry(AnthropicClaudeClient.ClaudeApiException.class)
                .build();
    }

    /**
     * Reader: Find opportunities without AI scores.
     *
     * Uses OpportunityRepository to find all active opportunities.
     * Processor will check if score exists before calling API.
     *
     * @return Item reader
     */
    @Bean
    public ItemReader<Opportunity> unscoredOpportunityReader() {
        return new RepositoryItemReaderBuilder<Opportunity>()
                .name("unscoredOpportunityReader")
                .repository(opportunityRepository)
                .methodName("findAll")
                .sorts(Map.of("postedDate", Sort.Direction.DESC))
                .pageSize(50)
                .build();
    }

    /**
     * Processor: Score opportunity using Claude API.
     *
     * Checks if opportunity already has an AI score. If yes, skips it (returns null).
     * If no, calls Claude API to score the opportunity and creates OpportunityScore entity.
     *
     * @return Item processor
     */
    @Bean
    public ItemProcessor<Opportunity, OpportunityScore> scoreProcessor() {
        return opportunity -> {
            try {
                // Check if already scored
                boolean hasScore = scoreRepository.existsByOpportunityIdAndScoreType(
                        opportunity.getId(),
                        "AI"
                );

                if (hasScore) {
                    logger.debug("Opportunity already scored: {}", opportunity.getNoticeId());
                    return null; // Skip this item
                }

                // Validate opportunity has required fields
                if (opportunity.getTitle() == null || opportunity.getTitle().isEmpty()) {
                    logger.warn("Opportunity missing title, skipping: {}", opportunity.getNoticeId());
                    return null;
                }

                String description = opportunity.getDescription() != null ?
                        opportunity.getDescription() : "No description available";

                logger.info("Scoring opportunity: {} ({})", opportunity.getTitle(), opportunity.getNoticeId());

                // Call Claude API to score
                AnthropicClaudeClient.OpportunityScoreResult result =
                        claudeClient.scoreOpportunity(
                                opportunity.getTitle(),
                                description,
                                companyCapabilities
                        );

                // Create OpportunityScore entity
                OpportunityScore score = new OpportunityScore();
                score.setOpportunityId(opportunity.getId());
                score.setScoreValue(BigDecimal.valueOf(result.getScore()));
                score.setScoreType("AI");
                score.setConfidence(BigDecimal.valueOf(calculateConfidenceValue(result.getScore())));

                // Store rationale in metadata
                score.setMetadata(Map.of("rationale", result.getRationale()));

                logger.info("Scored opportunity {} with score: {}", opportunity.getNoticeId(), result.getScore());

                return score;

            } catch (AnthropicClaudeClient.ClaudeApiException e) {
                logger.error("Claude API error scoring opportunity {}: {}", opportunity.getNoticeId(), e.getMessage());
                throw e; // Trigger retry
            } catch (Exception e) {
                logger.error("Unexpected error scoring opportunity {}", opportunity.getNoticeId(), e);
                return null; // Skip this item
            }
        };
    }

    /**
     * Writer: Save OpportunityScore entities to database.
     *
     * Bulk insert using scoreRepository.saveAll().
     *
     * @return Item writer
     */
    @Bean
    public ItemWriter<OpportunityScore> scoreWriter() {
        return chunk -> {
            // Filter out nulls (already scored/errors from processor)
            List<OpportunityScore> scores = chunk.getItems().stream()
                    .map(item -> (OpportunityScore) item)
                    .filter(score -> score != null)
                    .toList();

            if (!scores.isEmpty()) {
                scoreRepository.saveAll(scores);
                logger.info("Saved {} opportunity scores to database", scores.size());
            }
        };
    }

    /**
     * Calculate confidence value based on score.
     *
     * - High: score >= 80 → confidence 0.90
     * - Medium: score 50-79 → confidence 0.70
     * - Low: score < 50 → confidence 0.50
     */
    private double calculateConfidenceValue(int score) {
        if (score >= 80) {
            return 0.90;
        } else if (score >= 50) {
            return 0.70;
        } else {
            return 0.50;
        }
    }
}
