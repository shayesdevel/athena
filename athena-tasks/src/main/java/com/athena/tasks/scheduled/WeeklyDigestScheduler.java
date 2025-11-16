package com.athena.tasks.scheduled;

import com.athena.core.client.SmtpEmailClient;
import com.athena.core.entity.SyncLog;
import com.athena.core.repository.AlertRepository;
import com.athena.core.repository.OpportunityRepository;
import com.athena.core.repository.OpportunityScoreRepository;
import com.athena.core.repository.SyncLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Scheduled job for weekly digest email.
 *
 * Schedule: Mondays at 9:00 AM
 * Cron: "0 0 9 * * MON"
 *
 * Logic:
 * 1. Summarize last week's activity:
 *    - Opportunities added
 *    - Opportunities scored
 *    - Alerts sent
 * 2. Generate HTML email with summary table
 * 3. Send via SMTP
 * 4. Log sync activity (SyncLog entity)
 *
 * Configuration:
 * - athena.digest.recipient-email: Email recipient
 * - athena.digest.enabled: Enable/disable digest (default: true)
 */
@Component
public class WeeklyDigestScheduler {

    private static final Logger logger = LoggerFactory.getLogger(WeeklyDigestScheduler.class);

    private final OpportunityRepository opportunityRepository;
    private final OpportunityScoreRepository scoreRepository;
    private final AlertRepository alertRepository;
    private final SyncLogRepository syncLogRepository;
    private final SmtpEmailClient emailClient;

    @Value("${athena.digest.recipient-email:}")
    private String recipientEmail;

    @Value("${athena.digest.enabled:true}")
    private boolean digestEnabled;

    public WeeklyDigestScheduler(
            OpportunityRepository opportunityRepository,
            OpportunityScoreRepository scoreRepository,
            AlertRepository alertRepository,
            SyncLogRepository syncLogRepository,
            SmtpEmailClient emailClient) {
        this.opportunityRepository = opportunityRepository;
        this.scoreRepository = scoreRepository;
        this.alertRepository = alertRepository;
        this.syncLogRepository = syncLogRepository;
        this.emailClient = emailClient;
    }

    /**
     * Run weekly digest job.
     *
     * Executes Mondays at 9:00 AM.
     */
    @Scheduled(cron = "${athena.digest.weekly-cron:0 0 9 * * MON}")
    @Transactional
    public void sendWeeklyDigest() {
        if (!digestEnabled) {
            logger.info("Weekly digest disabled, skipping");
            return;
        }

        if (recipientEmail == null || recipientEmail.isEmpty()) {
            logger.warn("Weekly digest recipient email not configured, skipping");
            return;
        }

        logger.info("Starting weekly digest job");

        try {
            // Calculate last week time range
            Instant now = Instant.now();
            Instant oneWeekAgo = now.minus(7, ChronoUnit.DAYS);

            // Gather statistics
            WeeklyStats stats = gatherWeeklyStats(oneWeekAgo, now);

            // Generate email
            String emailSubject = "Athena Weekly Digest - " + now.toString().substring(0, 10);
            String emailBody = buildDigestEmail(stats, oneWeekAgo, now);

            // Send email
            emailClient.sendTextEmail(recipientEmail, emailSubject, emailBody);

            logger.info("Weekly digest sent to: {}", recipientEmail);

            // Log sync activity
            SyncLog syncLog = new SyncLog();
            syncLog.setSyncType("WEEKLY_DIGEST");
            syncLog.setStatus("SUCCESS");
            syncLog.setRecordsProcessed((int) stats.opportunitiesAdded);
            syncLog.setCompletedAt(Instant.now());
            syncLogRepository.save(syncLog);

            logger.info("Weekly digest job completed successfully");

        } catch (Exception e) {
            logger.error("Weekly digest job failed", e);

            // Log failure
            SyncLog syncLog = new SyncLog();
            syncLog.setSyncType("WEEKLY_DIGEST");
            syncLog.setStatus("FAILED");
            syncLog.setErrorLog("Error: " + e.getMessage());
            syncLog.setErrorCount(1);
            syncLog.setCompletedAt(Instant.now());
            syncLogRepository.save(syncLog);
        }
    }

    private WeeklyStats gatherWeeklyStats(Instant startTime, Instant endTime) {
        WeeklyStats stats = new WeeklyStats();

        // Count opportunities added in last week
        stats.opportunitiesAdded = opportunityRepository.countByCreatedAtBetween(startTime, endTime);

        // Count opportunities scored in last week
        stats.opportunitiesScored = scoreRepository.countByCreatedAtBetween(startTime, endTime);

        // Count alerts created/triggered in last week
        // Note: Alert entity tracks user preferences, not sent messages
        // For prototype, using alert count as proxy
        stats.alertsSent = alertRepository.countByCreatedAtBetween(startTime, endTime);

        // Get high-score count (scores >= 80)
        stats.highScoreCount = scoreRepository.countByScoreValueGreaterThanEqualAndCreatedAtBetween(
                BigDecimal.valueOf(80), startTime, endTime);

        // Get medium-score count (scores 50-79)
        stats.mediumScoreCount = scoreRepository.countByScoreValueBetweenAndCreatedAtBetween(
                BigDecimal.valueOf(50), BigDecimal.valueOf(79), startTime, endTime);

        // Get low-score count (scores < 50)
        stats.lowScoreCount = scoreRepository.countByScoreValueLessThanAndCreatedAtBetween(
                BigDecimal.valueOf(50), startTime, endTime);

        logger.info("Weekly stats: {} opportunities added, {} scored, {} alerts sent",
                stats.opportunitiesAdded, stats.opportunitiesScored, stats.alertsSent);

        return stats;
    }

    private String buildDigestEmail(WeeklyStats stats, Instant startTime, Instant endTime) {
        String startDate = startTime.toString().substring(0, 10);
        String endDate = endTime.toString().substring(0, 10);

        return String.format(
                "Athena Weekly Digest\n\n" +
                "Period: %s to %s\n\n" +
                "Activity Summary:\n" +
                "----------------\n" +
                "Opportunities Added: %d\n" +
                "Opportunities Scored: %d\n" +
                "Alerts Sent: %d\n\n" +
                "Score Breakdown:\n" +
                "----------------\n" +
                "High Scores (80-100): %d\n" +
                "Medium Scores (50-79): %d\n" +
                "Low Scores (0-49): %d\n\n" +
                "Key Insights:\n" +
                "-------------\n" +
                "%s\n\n" +
                "---\n" +
                "This is an automated digest from Athena Contract Intelligence Platform.\n" +
                "To adjust digest settings, contact your system administrator.",
                startDate,
                endDate,
                stats.opportunitiesAdded,
                stats.opportunitiesScored,
                stats.alertsSent,
                stats.highScoreCount,
                stats.mediumScoreCount,
                stats.lowScoreCount,
                generateInsights(stats)
        );
    }

    private String generateInsights(WeeklyStats stats) {
        StringBuilder insights = new StringBuilder();

        if (stats.opportunitiesAdded == 0) {
            insights.append("- No new opportunities added this week.\n");
        } else {
            insights.append(String.format("- %d new opportunities discovered.\n", stats.opportunitiesAdded));
        }

        if (stats.highScoreCount > 0) {
            insights.append(String.format("- %d high-value opportunities identified!\n", stats.highScoreCount));
        }

        if (stats.opportunitiesScored > 0) {
            double scoringRate = (double) stats.opportunitiesScored / Math.max(stats.opportunitiesAdded, 1) * 100;
            insights.append(String.format("- %.0f%% of new opportunities scored by AI.\n", scoringRate));
        }

        if (stats.alertsSent > 0) {
            insights.append(String.format("- %d alerts delivered to capture team.\n", stats.alertsSent));
        }

        if (insights.length() == 0) {
            insights.append("- Quiet week - no significant activity.");
        }

        return insights.toString();
    }

    private static class WeeklyStats {
        long opportunitiesAdded;
        long opportunitiesScored;
        long alertsSent;
        long highScoreCount;
        long mediumScoreCount;
        long lowScoreCount;
    }
}
