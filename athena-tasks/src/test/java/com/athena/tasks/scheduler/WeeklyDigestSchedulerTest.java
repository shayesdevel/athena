package com.athena.tasks.scheduler;

import com.athena.core.client.SmtpEmailClient;
import com.athena.core.entity.Opportunity;
import com.athena.core.entity.OpportunityScore;
import com.athena.core.entity.SyncLog;
import com.athena.core.repository.OpportunityRepository;
import com.athena.core.repository.OpportunityScoreRepository;
import com.athena.core.repository.SyncLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Integration tests for Weekly Digest Scheduler.
 * Tests digest generation, email formatting, and SyncLog creation.
 *
 * <p>Test scenarios:
 * <ul>
 *   <li>Digest generation with summary of last week's activity</li>
 *   <li>Email sending with HTML formatting</li>
 *   <li>SyncLog entity creation</li>
 *   <li>Empty digest handling (no activity)</li>
 *   <li>Proper date range filtering (last 7 days)</li>
 * </ul>
 */
@SpringBootTest
@ActiveProfiles("test")
class WeeklyDigestSchedulerTest {

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
    private OpportunityRepository opportunityRepository;

    @Autowired
    private OpportunityScoreRepository opportunityScoreRepository;

    @Autowired
    private SyncLogRepository syncLogRepository;

    @MockBean
    private SmtpEmailClient emailClient;

    // This will be injected once Backend Architect creates the scheduler
    // @Autowired
    // private WeeklyDigestScheduler scheduler;

    @BeforeEach
    void setUp() {
        syncLogRepository.deleteAll();
        opportunityScoreRepository.deleteAll();
        opportunityRepository.deleteAll();
    }

    @Test
    void shouldGenerateDigestWithLastWeekActivity() {
        // Arrange - Create opportunities from last week
        Instant lastWeek = Instant.now().minus(5, ChronoUnit.DAYS);

        Opportunity opp1 = createTestOpportunity("DIGEST-001", "Recent Opportunity 1");
        opp1.setPostedDate(lastWeek);
        Opportunity opp2 = createTestOpportunity("DIGEST-002", "Recent Opportunity 2");
        opp2.setPostedDate(lastWeek.plus(1, ChronoUnit.DAYS));

        opportunityRepository.saveAll(List.of(opp1, opp2));

        OpportunityScore score1 = new OpportunityScore(opp1.getId(), "relevance", new BigDecimal("85.0"));
        score1.setScoredAt(lastWeek.plus(1, ChronoUnit.HOURS));
        OpportunityScore score2 = new OpportunityScore(opp2.getId(), "relevance", new BigDecimal("90.0"));
        score2.setScoredAt(lastWeek.plus(25, ChronoUnit.HOURS));

        opportunityScoreRepository.saveAll(List.of(score1, score2));

        // Act
        // scheduler.sendWeeklyDigest();

        // Assert
        verify(emailClient, times(1)).sendEmail(anyString(), contains("Weekly Digest"), anyString());
    }

    @Test
    void shouldIncludeNewOpportunitiesInDigest() {
        // Arrange
        Instant lastWeek = Instant.now().minus(3, ChronoUnit.DAYS);

        Opportunity newOpp1 = createTestOpportunity("NEW-001", "New Opportunity 1");
        newOpp1.setPostedDate(lastWeek);
        Opportunity newOpp2 = createTestOpportunity("NEW-002", "New Opportunity 2");
        newOpp2.setPostedDate(lastWeek.plus(1, ChronoUnit.DAYS));

        opportunityRepository.saveAll(List.of(newOpp1, newOpp2));

        // Act
        // scheduler.sendWeeklyDigest();

        // Assert - Email should contain count of new opportunities
        verify(emailClient, times(1)).sendEmail(
                anyString(),
                anyString(),
                contains("2") // Should mention 2 new opportunities
        );
    }

    @Test
    void shouldIncludeHighScoresInDigest() {
        // Arrange
        Instant lastWeek = Instant.now().minus(4, ChronoUnit.DAYS);

        Opportunity highScoreOpp = createTestOpportunity("HIGH-001", "High Score Opportunity");
        opportunityRepository.save(highScoreOpp);

        OpportunityScore highScore = new OpportunityScore(
                highScoreOpp.getId(),
                "relevance",
                new BigDecimal("92.0")
        );
        highScore.setScoredAt(lastWeek);
        opportunityScoreRepository.save(highScore);

        // Act
        // scheduler.sendWeeklyDigest();

        // Assert - Email should highlight high scores
        verify(emailClient, times(1)).sendEmail(
                anyString(),
                anyString(),
                contains("92") // Should mention high score value
        );
    }

    @Test
    void shouldSendHtmlFormattedEmail() {
        // Arrange
        Instant lastWeek = Instant.now().minus(2, ChronoUnit.DAYS);

        Opportunity opp = createTestOpportunity("HTML-001", "Test Opportunity");
        opp.setPostedDate(lastWeek);
        opportunityRepository.save(opp);

        // Act
        // scheduler.sendWeeklyDigest();

        // Assert - Email body should contain HTML tags
        verify(emailClient, times(1)).sendEmail(
                anyString(),
                anyString(),
                contains("<html>") // HTML formatting
        );
    }

    @Test
    void shouldCreateSyncLogAfterSendingDigest() {
        // Arrange
        Instant lastWeek = Instant.now().minus(3, ChronoUnit.DAYS);

        Opportunity opp = createTestOpportunity("SYNC-001", "Test Opportunity");
        opp.setPostedDate(lastWeek);
        opportunityRepository.save(opp);

        // Act
        // scheduler.sendWeeklyDigest();

        // Assert
        List<SyncLog> syncLogs = syncLogRepository.findAll();
        assertThat(syncLogs).hasSize(1);

        SyncLog syncLog = syncLogs.get(0);
        assertThat(syncLog.getSyncType()).isEqualTo("weekly_digest");
        assertThat(syncLog.isSuccess()).isTrue();
        assertThat(syncLog.getSyncedAt()).isNotNull();
    }

    @Test
    void shouldHandleEmptyDigestGracefully() {
        // Arrange - No opportunities in last week
        Instant oldDate = Instant.now().minus(10, ChronoUnit.DAYS);

        Opportunity oldOpp = createTestOpportunity("OLD-001", "Old Opportunity");
        oldOpp.setPostedDate(oldDate);
        opportunityRepository.save(oldOpp);

        // Act
        // scheduler.sendWeeklyDigest();

        // Assert - Should still send email but with "no activity" message
        verify(emailClient, times(1)).sendEmail(
                anyString(),
                anyString(),
                contains("No new opportunities") // Or similar message
        );

        // Should still create SyncLog
        List<SyncLog> syncLogs = syncLogRepository.findAll();
        assertThat(syncLogs).hasSize(1);
    }

    @Test
    void shouldNotIncludeOldOpportunitiesInDigest() {
        // Arrange
        Instant oldDate = Instant.now().minus(15, ChronoUnit.DAYS); // More than 7 days ago
        Instant recentDate = Instant.now().minus(3, ChronoUnit.DAYS); // Within last 7 days

        Opportunity oldOpp = createTestOpportunity("OLD-001", "Old Opportunity");
        oldOpp.setPostedDate(oldDate);
        Opportunity recentOpp = createTestOpportunity("RECENT-001", "Recent Opportunity");
        recentOpp.setPostedDate(recentDate);

        opportunityRepository.saveAll(List.of(oldOpp, recentOpp));

        // Act
        // scheduler.sendWeeklyDigest();

        // Assert - Digest should only include recent opportunity
        verify(emailClient, times(1)).sendEmail(
                anyString(),
                anyString(),
                contains("RECENT-001") // Should include recent
        );

        // Should NOT include old opportunity
        verify(emailClient, never()).sendEmail(
                anyString(),
                anyString(),
                contains("OLD-001")
        );
    }

    @Test
    void shouldIncludeSummaryStatistics() {
        // Arrange
        Instant lastWeek = Instant.now().minus(4, ChronoUnit.DAYS);

        // Create 5 opportunities with varying scores
        for (int i = 1; i <= 5; i++) {
            Opportunity opp = createTestOpportunity("STAT-00" + i, "Opportunity " + i);
            opp.setPostedDate(lastWeek.plus(i, ChronoUnit.HOURS));
            opportunityRepository.save(opp);

            OpportunityScore score = new OpportunityScore(
                    opp.getId(),
                    "relevance",
                    new BigDecimal(70 + (i * 3)) // 73, 76, 79, 82, 85
            );
            score.setScoredAt(lastWeek.plus(i + 1, ChronoUnit.HOURS));
            opportunityScoreRepository.save(score);
        }

        // Act
        // scheduler.sendWeeklyDigest();

        // Assert - Email should contain summary stats
        verify(emailClient, times(1)).sendEmail(
                anyString(),
                anyString(),
                contains("5") // Total opportunities
        );
    }

    @Test
    void shouldHandleEmailSendingFailuresGracefully() {
        // Arrange
        Instant lastWeek = Instant.now().minus(2, ChronoUnit.DAYS);

        Opportunity opp = createTestOpportunity("FAIL-001", "Test Opportunity");
        opp.setPostedDate(lastWeek);
        opportunityRepository.save(opp);

        // Mock email failure
        // when(emailClient.sendEmail(anyString(), anyString(), anyString()))
        //     .thenThrow(new RuntimeException("SMTP server unavailable"));

        // Act
        // scheduler.sendWeeklyDigest();

        // Assert - Should still create SyncLog but mark as failed
        List<SyncLog> syncLogs = syncLogRepository.findAll();
        assertThat(syncLogs).hasSize(1);

        SyncLog syncLog = syncLogs.get(0);
        assertThat(syncLog.isSuccess()).isFalse();
        assertThat(syncLog.getErrorMessage()).contains("SMTP");
    }

    @Test
    void shouldSendToConfiguredRecipients() {
        // Arrange
        Instant lastWeek = Instant.now().minus(3, ChronoUnit.DAYS);

        Opportunity opp = createTestOpportunity("RECIPIENT-001", "Test Opportunity");
        opp.setPostedDate(lastWeek);
        opportunityRepository.save(opp);

        // Act
        // scheduler.sendWeeklyDigest();

        // Assert - Email should be sent to configured recipient(s)
        verify(emailClient, times(1)).sendEmail(
                contains("@"), // Should be a valid email address
                anyString(),
                anyString()
        );
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
