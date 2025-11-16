package com.athena.tasks.scheduled;

import com.athena.core.client.MicrosoftTeamsClient;
import com.athena.core.client.SmtpEmailClient;
import com.athena.core.entity.Alert;
import com.athena.core.entity.Opportunity;
import com.athena.core.entity.OpportunityScore;
import com.athena.core.repository.AlertRepository;
import com.athena.core.repository.OpportunityRepository;
import com.athena.core.repository.OpportunityScoreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Scheduled job for high-score opportunity alerts.
 *
 * Schedule: Weekdays at 8:00 AM (Monday-Friday)
 * Cron: "0 0 8 * * MON-FRI"
 *
 * Logic:
 * 1. Find opportunities scored > threshold in last 24 hours
 * 2. Send Microsoft Teams adaptive card alert
 * 3. Send email alert
 * 4. Save Alert entity (track sent alerts)
 *
 * Configuration:
 * - athena.alerts.high-score-threshold: Score threshold (default: 80)
 * - athena.alerts.lookback-hours: Lookback period (default: 24)
 * - athena.alerts.recipient-email: Email recipient
 */
@Component
public class HighScoreAlertScheduler {

    private static final Logger logger = LoggerFactory.getLogger(HighScoreAlertScheduler.class);

    private final OpportunityScoreRepository scoreRepository;
    private final OpportunityRepository opportunityRepository;
    private final AlertRepository alertRepository;
    private final MicrosoftTeamsClient teamsClient;
    private final SmtpEmailClient emailClient;

    @Value("${athena.alerts.high-score-threshold:80}")
    private int scoreThreshold;

    @Value("${athena.alerts.lookback-hours:24}")
    private int lookbackHours;

    @Value("${athena.alerts.recipient-email:}")
    private String recipientEmail;

    @Value("${athena.alerts.enabled:true}")
    private boolean alertsEnabled;

    public HighScoreAlertScheduler(
            OpportunityScoreRepository scoreRepository,
            OpportunityRepository opportunityRepository,
            AlertRepository alertRepository,
            MicrosoftTeamsClient teamsClient,
            SmtpEmailClient emailClient) {
        this.scoreRepository = scoreRepository;
        this.opportunityRepository = opportunityRepository;
        this.alertRepository = alertRepository;
        this.teamsClient = teamsClient;
        this.emailClient = emailClient;
    }

    /**
     * Run high-score alert job.
     *
     * Executes weekdays at 8:00 AM.
     */
    @Scheduled(cron = "${athena.alerts.high-score-cron:0 0 8 * * MON-FRI}")
    @Transactional
    public void sendHighScoreAlerts() {
        if (!alertsEnabled) {
            logger.info("High-score alerts disabled, skipping");
            return;
        }

        logger.info("Starting high-score alert job (threshold: {}, lookback: {} hours)", scoreThreshold, lookbackHours);

        try {
            // Calculate lookback time
            Instant lookbackTime = Instant.now().minus(lookbackHours, ChronoUnit.HOURS);

            // Find high-scoring opportunities in last 24 hours
            List<OpportunityScore> highScores = scoreRepository.findByScoreValueGreaterThanEqualAndCreatedAtAfter(
                    BigDecimal.valueOf(scoreThreshold),
                    lookbackTime
            );

            if (highScores.isEmpty()) {
                logger.info("No high-scoring opportunities found in last {} hours", lookbackHours);
                return;
            }

            logger.info("Found {} high-scoring opportunities", highScores.size());

            // Send alerts for each high-scoring opportunity
            for (OpportunityScore score : highScores) {
                try {
                    sendAlertsForOpportunity(score);
                } catch (Exception e) {
                    logger.error("Failed to send alert for opportunity: {}", score.getOpportunityId(), e);
                }
            }

            logger.info("High-score alert job completed successfully");

        } catch (Exception e) {
            logger.error("High-score alert job failed", e);
        }
    }

    private void sendAlertsForOpportunity(OpportunityScore score) {
        // Fetch the opportunity
        Opportunity opportunity = opportunityRepository.findById(score.getOpportunityId())
                .orElseThrow(() -> new RuntimeException("Opportunity not found: " + score.getOpportunityId()));

        // Note: Alert entity tracks user preferences, not sent alerts
        // For prototype, we'll send alerts each time job runs
        // Production system would need a separate SentAlert tracking table

        logger.info("Sending alerts for opportunity: {} (score: {})", opportunity.getTitle(), score.getScoreValue());

        // Send Teams notification
        try {
            String teamsTitle = String.format("High-Score Opportunity: %s", opportunity.getTitle());
            String teamsMessage = buildTeamsMessage(opportunity, score);
            teamsClient.sendMessage(teamsTitle, teamsMessage);
            logger.info("Sent Teams alert for opportunity: {}", opportunity.getNoticeId());
        } catch (Exception e) {
            logger.error("Failed to send Teams alert: {}", e.getMessage());
        }

        // Send email alert
        if (recipientEmail != null && !recipientEmail.isEmpty()) {
            try {
                String emailSubject = String.format("High-Score Opportunity Alert: %s (Score: %.0f)",
                        opportunity.getTitle(), score.getScoreValue().doubleValue());
                String emailBody = buildEmailMessage(opportunity, score);
                emailClient.sendTextEmail(recipientEmail, emailSubject, emailBody);
                logger.info("Sent email alert for opportunity: {}", opportunity.getNoticeId());
            } catch (Exception e) {
                logger.error("Failed to send email alert: {}", e.getMessage());
            }
        }

        logger.info("Alert sent for opportunity: {}", opportunity.getNoticeId());
    }

    private String buildTeamsMessage(Opportunity opportunity, OpportunityScore score) {
        String rationale = score.getMetadata() != null && score.getMetadata().containsKey("rationale")
                ? score.getMetadata().get("rationale").toString()
                : "No rationale provided";

        return String.format(
                "**High-Score Opportunity Alert**\n\n" +
                "**Title**: %s\n\n" +
                "**Score**: %.0f/100 (AI Confidence: %.0f%%)\n\n" +
                "**Agency**: %s\n\n" +
                "**Posted**: %s\n\n" +
                "**Deadline**: %s\n\n" +
                "**Rationale**: %s\n\n" +
                "**Link**: %s",
                opportunity.getTitle(),
                score.getScoreValue().doubleValue(),
                score.getConfidence() != null ? score.getConfidence().doubleValue() * 100 : 0,
                opportunity.getAgency() != null ? opportunity.getAgency().getName() : "Unknown",
                opportunity.getPostedDate(),
                opportunity.getResponseDeadline(),
                rationale,
                opportunity.getUiLink() != null ? opportunity.getUiLink() : "N/A"
        );
    }

    private String buildEmailMessage(Opportunity opportunity, OpportunityScore score) {
        String rationale = score.getMetadata() != null && score.getMetadata().containsKey("rationale")
                ? score.getMetadata().get("rationale").toString()
                : "No rationale provided";

        return String.format(
                "High-Score Opportunity Alert\n\n" +
                "Title: %s\n\n" +
                "AI Score: %.0f/100 (Confidence: %.0f%%)\n\n" +
                "Agency: %s\n" +
                "Posted Date: %s\n" +
                "Response Deadline: %s\n\n" +
                "Rationale:\n%s\n\n" +
                "View Opportunity: %s\n\n" +
                "---\n" +
                "This is an automated alert from Athena Contract Intelligence Platform.",
                opportunity.getTitle(),
                score.getScoreValue().doubleValue(),
                score.getConfidence() != null ? score.getConfidence().doubleValue() * 100 : 0,
                opportunity.getAgency() != null ? opportunity.getAgency().getName() : "Unknown",
                opportunity.getPostedDate(),
                opportunity.getResponseDeadline(),
                rationale,
                opportunity.getUiLink() != null ? opportunity.getUiLink() : "N/A"
        );
    }
}
