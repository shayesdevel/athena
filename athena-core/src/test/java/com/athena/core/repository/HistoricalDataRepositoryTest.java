package com.athena.core.repository;

import com.athena.core.AbstractIntegrationTest;
import com.athena.core.entity.HistoricalData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class HistoricalDataRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private HistoricalDataRepository historicalDataRepository;

    @Test
    void shouldSaveAndRetrieveHistoricalData() {
        // Arrange
        UUID entityId = UUID.randomUUID();
        Map<String, Object> dataValue = Map.of(
                "awardAmount", 1500000,
                "awardedTo", "Acme Corp",
                "awardDate", "2024-01-15"
        );

        HistoricalData historical = new HistoricalData("award", entityId, "award_snapshot", dataValue);
        historical.setCapturedAt(Instant.now());

        // Act
        HistoricalData saved = historicalDataRepository.save(historical);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEntityType()).isEqualTo("award");
        assertThat(saved.getEntityId()).isEqualTo(entityId);
        assertThat(saved.getDataType()).isEqualTo("award_snapshot");
        assertThat(saved.getDataValue()).containsEntry("awardAmount", 1500000);
        assertThat(saved.getCapturedAt()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldFindByEntityTypeAndEntityId() {
        // Arrange
        UUID entityId = UUID.randomUUID();
        Map<String, Object> data1 = Map.of("score", 85);
        Map<String, Object> data2 = Map.of("score", 90);

        historicalDataRepository.save(new HistoricalData("opportunity", entityId, "score_history", data1));
        historicalDataRepository.save(new HistoricalData("opportunity", entityId, "score_history", data2));

        // Act
        List<HistoricalData> history = historicalDataRepository.findByEntityTypeAndEntityId("opportunity", entityId);

        // Assert
        assertThat(history).hasSize(2);
        assertThat(history).extracting(HistoricalData::getEntityId)
                .containsOnly(entityId);
    }

    @Test
    void shouldFindByEntityTypeAndEntityIdAndDataType() {
        // Arrange
        UUID entityId = UUID.randomUUID();
        Map<String, Object> statusData = Map.of("status", "active");
        Map<String, Object> scoreData = Map.of("score", 75);

        historicalDataRepository.save(new HistoricalData("opportunity", entityId, "status_change", statusData));
        historicalDataRepository.save(new HistoricalData("opportunity", entityId, "score_history", scoreData));

        // Act
        List<HistoricalData> statusHistory = historicalDataRepository
                .findByEntityTypeAndEntityIdAndDataType("opportunity", entityId, "status_change");

        // Assert
        assertThat(statusHistory).hasSize(1);
        assertThat(statusHistory.get(0).getDataType()).isEqualTo("status_change");
    }

    @Test
    void shouldFindByEntityAndTimeRange() {
        // Arrange
        UUID entityId = UUID.randomUUID();
        Instant now = Instant.now();
        Instant oneHourAgo = now.minus(1, ChronoUnit.HOURS);
        Instant twoHoursAgo = now.minus(2, ChronoUnit.HOURS);
        Instant threeDaysAgo = now.minus(3, ChronoUnit.DAYS);

        HistoricalData recent1 = new HistoricalData("organization", entityId, "award_trends", Map.of("count", 5));
        recent1.setCapturedAt(oneHourAgo);
        historicalDataRepository.save(recent1);

        HistoricalData recent2 = new HistoricalData("organization", entityId, "award_trends", Map.of("count", 6));
        recent2.setCapturedAt(twoHoursAgo);
        historicalDataRepository.save(recent2);

        HistoricalData old = new HistoricalData("organization", entityId, "award_trends", Map.of("count", 3));
        old.setCapturedAt(threeDaysAgo);
        historicalDataRepository.save(old);

        // Act
        List<HistoricalData> recentHistory = historicalDataRepository
                .findByEntityAndTimeRange("organization", entityId, twoHoursAgo.minus(1, ChronoUnit.MINUTES), now);

        // Assert
        assertThat(recentHistory).hasSize(2);
        assertThat(recentHistory).allMatch(h -> h.getCapturedAt().isAfter(threeDaysAgo));
    }

    @Test
    void shouldCheckIfHistoricalDataExists() {
        // Arrange
        UUID entityId = UUID.randomUUID();
        Map<String, Object> data = Map.of("metric", "value");

        historicalDataRepository.save(new HistoricalData("opportunity", entityId, "metrics", data));

        // Act
        boolean exists = historicalDataRepository.existsByEntityTypeAndEntityId("opportunity", entityId);
        boolean notExists = historicalDataRepository.existsByEntityTypeAndEntityId("opportunity", UUID.randomUUID());

        // Assert
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}
