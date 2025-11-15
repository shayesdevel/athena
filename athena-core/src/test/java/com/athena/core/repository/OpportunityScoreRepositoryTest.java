package com.athena.core.repository;

import com.athena.core.AbstractIntegrationTest;
import com.athena.core.entity.OpportunityScore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OpportunityScoreRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private OpportunityScoreRepository opportunityScoreRepository;

    @Test
    void shouldSaveAndRetrieveOpportunityScore() {
        // Arrange
        UUID opportunityId = UUID.randomUUID();
        OpportunityScore score = new OpportunityScore(
                opportunityId,
                "relevance",
                new BigDecimal("85.50")
        );
        score.setConfidence(new BigDecimal("92.00"));
        score.setMetadata(Map.of("modelVersion", "1.0", "features", List.of("naics", "keywords")));

        // Act
        OpportunityScore saved = opportunityScoreRepository.save(score);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getOpportunityId()).isEqualTo(opportunityId);
        assertThat(saved.getScoreType()).isEqualTo("relevance");
        assertThat(saved.getScoreValue()).isEqualByComparingTo(new BigDecimal("85.50"));
        assertThat(saved.getConfidence()).isEqualByComparingTo(new BigDecimal("92.00"));
        assertThat(saved.getScoredAt()).isNotNull();
        assertThat(saved.getMetadata()).containsEntry("modelVersion", "1.0");
    }

    @Test
    void shouldFindByOpportunityId() {
        // Arrange
        UUID opportunityId = UUID.randomUUID();

        opportunityScoreRepository.save(new OpportunityScore(opportunityId, "relevance", new BigDecimal("80.00")));
        opportunityScoreRepository.save(new OpportunityScore(opportunityId, "win_probability", new BigDecimal("65.00")));

        // Act
        List<OpportunityScore> scores = opportunityScoreRepository.findByOpportunityId(opportunityId);

        // Assert
        assertThat(scores).hasSize(2);
        assertThat(scores).extracting(OpportunityScore::getOpportunityId)
                .containsOnly(opportunityId);
    }

    @Test
    void shouldFindByOpportunityIdAndScoreType() {
        // Arrange
        UUID opportunityId = UUID.randomUUID();

        opportunityScoreRepository.save(new OpportunityScore(opportunityId, "relevance", new BigDecimal("75.00")));
        opportunityScoreRepository.save(new OpportunityScore(opportunityId, "strategic_fit", new BigDecimal("88.00")));

        // Act
        List<OpportunityScore> relevanceScores = opportunityScoreRepository
                .findByOpportunityIdAndScoreType(opportunityId, "relevance");

        // Assert
        assertThat(relevanceScores).hasSize(1);
        assertThat(relevanceScores.get(0).getScoreType()).isEqualTo("relevance");
        assertThat(relevanceScores.get(0).getScoreValue()).isEqualByComparingTo(new BigDecimal("75.00"));
    }

    @Test
    void shouldFindLatestScoreByType() throws InterruptedException {
        // Arrange
        UUID opportunityId = UUID.randomUUID();

        OpportunityScore older = new OpportunityScore(opportunityId, "relevance", new BigDecimal("70.00"));
        older.setScoredAt(Instant.now().minusSeconds(60));
        opportunityScoreRepository.save(older);

        Thread.sleep(10); // Ensure different timestamps

        OpportunityScore newer = new OpportunityScore(opportunityId, "relevance", new BigDecimal("85.00"));
        newer.setScoredAt(Instant.now());
        opportunityScoreRepository.save(newer);

        // Act
        var latest = opportunityScoreRepository
                .findLatestByOpportunityIdAndScoreType(opportunityId, "relevance");

        // Assert
        assertThat(latest).isPresent();
        assertThat(latest.get().getScoreValue()).isEqualByComparingTo(new BigDecimal("85.00"));
    }

    @Test
    void shouldFindByScoreType() {
        // Arrange
        opportunityScoreRepository.save(new OpportunityScore(UUID.randomUUID(), "win_probability", new BigDecimal("60.00")));
        opportunityScoreRepository.save(new OpportunityScore(UUID.randomUUID(), "win_probability", new BigDecimal("75.00")));

        // Act
        List<OpportunityScore> scores = opportunityScoreRepository.findByScoreType("win_probability");

        // Assert
        assertThat(scores).hasSizeGreaterThanOrEqualTo(2);
        assertThat(scores).extracting(OpportunityScore::getScoreType)
                .containsOnly("win_probability");
    }

    @Test
    void shouldCheckIfScoresExist() {
        // Arrange
        UUID opportunityId = UUID.randomUUID();
        opportunityScoreRepository.save(new OpportunityScore(opportunityId, "relevance", new BigDecimal("82.00")));

        // Act
        boolean exists = opportunityScoreRepository.existsByOpportunityId(opportunityId);
        boolean notExists = opportunityScoreRepository.existsByOpportunityId(UUID.randomUUID());

        // Assert
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}
