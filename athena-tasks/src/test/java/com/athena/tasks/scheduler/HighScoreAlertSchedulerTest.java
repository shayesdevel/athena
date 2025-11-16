package com.athena.tasks.scheduler;

import com.athena.core.client.MicrosoftTeamsClient;
import com.athena.core.client.SmtpEmailClient;
import com.athena.core.entity.Alert;
import com.athena.core.entity.Opportunity;
import com.athena.core.entity.OpportunityScore;
import com.athena.core.repository.AlertRepository;
import com.athena.core.repository.OpportunityRepository;
import com.athena.core.repository.OpportunityScoreRepository;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Integration tests for High Score Alert Scheduler.
 * Tests alert generation and notification delivery for high-scoring opportunities.
 *
 * <p>Test scenarios:
 * <ul>
 *   <li>Alert creation for opportunities with score > 80</li>
 *   <li>Teams notification delivery</li>
 *   <li>Email notification delivery</li>
 *   <li>Alert entity persistence</li>
 *   <li>Deduplication (no duplicate alerts)</li>
 *   <li>No alerts when no high scores</li>
 * </ul>
 */
@SpringBootTest
@ActiveProfiles("test")
class HighScoreAlertSchedulerTest {

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
    private AlertRepository alertRepository;

    @MockBean
    private MicrosoftTeamsClient teamsClient;

    @MockBean
    private SmtpEmailClient emailClient;

    // This will be injected once Backend Architect creates the scheduler
    // @Autowired
    // private HighScoreAlertScheduler scheduler;

    @BeforeEach
    void setUp() {
        alertRepository.deleteAll();
        opportunityScoreRepository.deleteAll();
        opportunityRepository.deleteAll();
    }

    @Test
    void shouldCreateAlertsForHighScoringOpportunities() {
        // Arrange
        Opportunity highScoreOpp = createTestOpportunity("HIGH-001", "High Score Opportunity");
        opportunityRepository.save(highScoreOpp);

        OpportunityScore highScore = new OpportunityScore(
                highScoreOpp.getId(),
                "relevance",
                new BigDecimal("85.0")
        );
        highScore.setConfidence(new BigDecimal("92.0"));
        opportunityScoreRepository.save(highScore);

        // Act
        // scheduler.checkForHighScoreAlerts();

        // Assert
        List<Alert> alerts = alertRepository.findAll();
        assertThat(alerts).hasSize(1);

        Alert alert = alerts.get(0);
        assertThat(alert.getOpportunityId()).isEqualTo(highScoreOpp.getId());
        assertThat(alert.getAlertType()).isEqualTo("high_score");
        assertThat(alert.isRead()).isFalse();
        assertThat(alert.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldSendTeamsNotificationForHighScores() {
        // Arrange
        Opportunity highScoreOpp = createTestOpportunity("HIGH-001", "Critical Opportunity");
        opportunityRepository.save(highScoreOpp);

        OpportunityScore highScore = new OpportunityScore(
                highScoreOpp.getId(),
                "relevance",
                new BigDecimal("90.0")
        );
        opportunityScoreRepository.save(highScore);

        // Act
        // scheduler.checkForHighScoreAlerts();

        // Assert
        verify(teamsClient, times(1)).sendMessage(anyString(), anyString());
    }

    @Test
    void shouldSendEmailNotificationForHighScores() {
        // Arrange
        Opportunity highScoreOpp = createTestOpportunity("HIGH-001", "Important Opportunity");
        opportunityRepository.save(highScoreOpp);

        OpportunityScore highScore = new OpportunityScore(
                highScoreOpp.getId(),
                "relevance",
                new BigDecimal("88.0")
        );
        opportunityScoreRepository.save(highScore);

        // Act
        // scheduler.checkForHighScoreAlerts();

        // Assert
        verify(emailClient, times(1)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void shouldNotCreateAlertsForLowScoringOpportunities() {
        // Arrange
        Opportunity lowScoreOpp = createTestOpportunity("LOW-001", "Low Score Opportunity");
        opportunityRepository.save(lowScoreOpp);

        OpportunityScore lowScore = new OpportunityScore(
                lowScoreOpp.getId(),
                "relevance",
                new BigDecimal("60.0")
        );
        opportunityScoreRepository.save(lowScore);

        // Act
        // scheduler.checkForHighScoreAlerts();

        // Assert
        List<Alert> alerts = alertRepository.findAll();
        assertThat(alerts).isEmpty();

        verify(teamsClient, never()).sendMessage(anyString(), anyString());
        verify(emailClient, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void shouldNotDuplicateAlertsForSameOpportunity() {
        // Arrange
        Opportunity highScoreOpp = createTestOpportunity("HIGH-001", "High Score Opportunity");
        opportunityRepository.save(highScoreOpp);

        OpportunityScore highScore = new OpportunityScore(
                highScoreOpp.getId(),
                "relevance",
                new BigDecimal("85.0")
        );
        opportunityScoreRepository.save(highScore);

        // Act - Run scheduler twice
        // scheduler.checkForHighScoreAlerts();
        // scheduler.checkForHighScoreAlerts();

        // Assert - Should only create one alert
        List<Alert> alerts = alertRepository.findAll();
        assertThat(alerts).hasSize(1);

        // Should only send notification once
        verify(teamsClient, times(1)).sendMessage(anyString(), anyString());
    }

    @Test
    void shouldHandleMultipleHighScoringOpportunities() {
        // Arrange
        Opportunity opp1 = createTestOpportunity("HIGH-001", "Opportunity 1");
        Opportunity opp2 = createTestOpportunity("HIGH-002", "Opportunity 2");
        Opportunity opp3 = createTestOpportunity("HIGH-003", "Opportunity 3");
        opportunityRepository.saveAll(List.of(opp1, opp2, opp3));

        opportunityScoreRepository.save(new OpportunityScore(opp1.getId(), "relevance", new BigDecimal("85.0")));
        opportunityScoreRepository.save(new OpportunityScore(opp2.getId(), "relevance", new BigDecimal("90.0")));
        opportunityScoreRepository.save(new OpportunityScore(opp3.getId(), "relevance", new BigDecimal("82.0")));

        // Act
        // scheduler.checkForHighScoreAlerts();

        // Assert
        List<Alert> alerts = alertRepository.findAll();
        assertThat(alerts).hasSize(3);

        // Should send 3 notifications
        verify(teamsClient, times(3)).sendMessage(anyString(), anyString());
        verify(emailClient, times(3)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void shouldIncludeOpportunityDetailsInAlert() {
        // Arrange
        Opportunity highScoreOpp = createTestOpportunity("HIGH-001", "Critical Contract");
        highScoreOpp.setSolicitationNumber("SOL-2025-001");
        highScoreOpp.setDepartment("Department of Defense");
        opportunityRepository.save(highScoreOpp);

        OpportunityScore highScore = new OpportunityScore(
                highScoreOpp.getId(),
                "relevance",
                new BigDecimal("92.0")
        );
        opportunityScoreRepository.save(highScore);

        // Act
        // scheduler.checkForHighScoreAlerts();

        // Assert
        List<Alert> alerts = alertRepository.findAll();
        assertThat(alerts).hasSize(1);

        Alert alert = alerts.get(0);
        // Alert should contain opportunity details in message or metadata
        assertThat(alert.getMessage()).contains("HIGH-001");
    }

    @Test
    void shouldHandleNotificationFailuresGracefully() {
        // Arrange
        Opportunity highScoreOpp = createTestOpportunity("HIGH-001", "Test Opportunity");
        opportunityRepository.save(highScoreOpp);

        OpportunityScore highScore = new OpportunityScore(
                highScoreOpp.getId(),
                "relevance",
                new BigDecimal("85.0")
        );
        opportunityScoreRepository.save(highScore);

        // Mock notification failure
        // when(teamsClient.sendMessage(anyString(), anyString()))
        //     .thenThrow(new RuntimeException("Teams API unavailable"));

        // Act
        // scheduler.checkForHighScoreAlerts();

        // Assert - Alert should still be created even if notification fails
        List<Alert> alerts = alertRepository.findAll();
        assertThat(alerts).hasSize(1);
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
