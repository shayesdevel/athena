package com.athena.core.repository;

import com.athena.core.AbstractIntegrationTest;
import com.athena.core.entity.Alert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AlertRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private AlertRepository alertRepository;

    @Test
    void shouldSaveAndRetrieveAlert() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Map<String, Object> criteria = new HashMap<>();
        criteria.put("naicsCode", "541511");
        criteria.put("minValue", 100000);

        Alert alert = new Alert(userId, "new_opportunity", criteria, "daily");

        // Act
        Alert saved = alertRepository.save(alert);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUserId()).isEqualTo(userId);
        assertThat(saved.getAlertType()).isEqualTo("new_opportunity");
        assertThat(saved.getFrequency()).isEqualTo("daily");
        assertThat(saved.getCriteria()).containsEntry("naicsCode", "541511");
        assertThat(saved.getIsActive()).isTrue();
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldFindByUserId() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Map<String, Object> criteria1 = Map.of("keyword", "AI");
        Map<String, Object> criteria2 = Map.of("keyword", "cloud");

        alertRepository.save(new Alert(userId, "new_opportunity", criteria1, "realtime"));
        alertRepository.save(new Alert(userId, "deadline_reminder", criteria2, "daily"));

        // Act
        List<Alert> alerts = alertRepository.findByUserId(userId);

        // Assert
        assertThat(alerts).hasSize(2);
        assertThat(alerts).extracting(Alert::getUserId)
                .containsOnly(userId);
    }

    @Test
    void shouldFindActiveAlertsByUserId() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Map<String, Object> criteria = Map.of("agency", "DOD");

        Alert active = new Alert(userId, "new_opportunity", criteria, "weekly");
        active.setIsActive(true);
        alertRepository.save(active);

        Alert inactive = new Alert(userId, "new_opportunity", criteria, "weekly");
        inactive.setIsActive(false);
        alertRepository.save(inactive);

        // Act
        List<Alert> activeAlerts = alertRepository.findByUserIdAndIsActive(userId, true);

        // Assert
        assertThat(activeAlerts).hasSize(1);
        assertThat(activeAlerts.get(0).getIsActive()).isTrue();
    }

    @Test
    void shouldFindByAlertType() {
        // Arrange
        Map<String, Object> criteria = Map.of("keyword", "cybersecurity");

        alertRepository.save(new Alert(UUID.randomUUID(), "deadline_reminder", criteria, "daily"));
        alertRepository.save(new Alert(UUID.randomUUID(), "deadline_reminder", criteria, "weekly"));

        // Act
        List<Alert> alerts = alertRepository.findByAlertType("deadline_reminder");

        // Assert
        assertThat(alerts).hasSizeGreaterThanOrEqualTo(2);
        assertThat(alerts).extracting(Alert::getAlertType)
                .containsOnly("deadline_reminder");
    }

    @Test
    void shouldFindActiveAlertsByType() {
        // Arrange
        Map<String, Object> criteria = Map.of("setAside", "8(a)");

        Alert active = new Alert(UUID.randomUUID(), "new_award", criteria, "daily");
        active.setIsActive(true);
        alertRepository.save(active);

        Alert inactive = new Alert(UUID.randomUUID(), "new_award", criteria, "daily");
        inactive.setIsActive(false);
        alertRepository.save(inactive);

        // Act
        List<Alert> activeAlerts = alertRepository.findByAlertTypeAndIsActive("new_award", true);

        // Assert
        assertThat(activeAlerts).hasSizeGreaterThanOrEqualTo(1);
        assertThat(activeAlerts).allMatch(Alert::getIsActive);
    }

    @Test
    void shouldCheckIfActiveAlertsExist() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Map<String, Object> criteria = Map.of("naicsCode", "541512");

        Alert alert = new Alert(userId, "new_opportunity", criteria, "realtime");
        alert.setIsActive(true);
        alertRepository.save(alert);

        // Act
        boolean exists = alertRepository.existsByUserIdAndIsActive(userId, true);
        boolean notExists = alertRepository.existsByUserIdAndIsActive(UUID.randomUUID(), true);

        // Assert
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void shouldUpdateLastTriggeredTimestamp() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Map<String, Object> criteria = Map.of("keyword", "AI");
        Alert alert = new Alert(userId, "new_opportunity", criteria, "daily");
        Alert saved = alertRepository.save(alert);

        assertThat(saved.getLastTriggered()).isNull();

        // Act
        saved.setLastTriggered(java.time.Instant.now());
        Alert updated = alertRepository.save(saved);

        // Assert
        assertThat(updated.getLastTriggered()).isNotNull();
    }
}
