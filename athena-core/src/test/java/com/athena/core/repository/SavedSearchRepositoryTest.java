package com.athena.core.repository;

import com.athena.core.AbstractIntegrationTest;
import com.athena.core.entity.SavedSearch;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SavedSearchRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private SavedSearchRepository savedSearchRepository;

    @Test
    void shouldSaveAndRetrieveSavedSearch() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Map<String, Object> criteria = new HashMap<>();
        criteria.put("keyword", "cybersecurity");
        criteria.put("naicsCode", "541512");

        SavedSearch search = new SavedSearch(userId, "Cybersecurity Opportunities", criteria);

        // Act
        SavedSearch saved = savedSearchRepository.save(search);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUserId()).isEqualTo(userId);
        assertThat(saved.getSearchName()).isEqualTo("Cybersecurity Opportunities");
        assertThat(saved.getSearchCriteria()).containsEntry("keyword", "cybersecurity");
        assertThat(saved.getIsActive()).isTrue();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldFindByUserId() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Map<String, Object> criteria1 = Map.of("naicsCode", "541511");
        Map<String, Object> criteria2 = Map.of("naicsCode", "541512");

        savedSearchRepository.save(new SavedSearch(userId, "Search 1", criteria1));
        savedSearchRepository.save(new SavedSearch(userId, "Search 2", criteria2));

        // Act
        List<SavedSearch> searches = savedSearchRepository.findByUserId(userId);

        // Assert
        assertThat(searches).hasSize(2);
        assertThat(searches).extracting(SavedSearch::getUserId)
                .containsOnly(userId);
    }

    @Test
    void shouldFindActiveSearchesByUserId() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Map<String, Object> criteria = Map.of("keyword", "AI");

        SavedSearch active = new SavedSearch(userId, "Active Search", criteria);
        active.setIsActive(true);
        savedSearchRepository.save(active);

        SavedSearch inactive = new SavedSearch(userId, "Inactive Search", criteria);
        inactive.setIsActive(false);
        savedSearchRepository.save(inactive);

        // Act
        List<SavedSearch> activeSearches = savedSearchRepository.findByUserIdAndIsActive(userId, true);

        // Assert
        assertThat(activeSearches).hasSize(1);
        assertThat(activeSearches.get(0).getSearchName()).isEqualTo("Active Search");
    }

    @Test
    void shouldFindByUserIdAndSearchName() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Map<String, Object> criteria = Map.of("agency", "DOD");
        SavedSearch search = new SavedSearch(userId, "DOD Opportunities", criteria);
        savedSearchRepository.save(search);

        // Act
        var found = savedSearchRepository.findByUserIdAndSearchName(userId, "DOD Opportunities");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getSearchName()).isEqualTo("DOD Opportunities");
        assertThat(found.get().getSearchCriteria()).containsEntry("agency", "DOD");
    }

    @Test
    void shouldCheckIfSearchNameExists() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Map<String, Object> criteria = Map.of("setAside", "8(a)");
        SavedSearch search = new SavedSearch(userId, "8(a) Opportunities", criteria);
        savedSearchRepository.save(search);

        // Act
        boolean exists = savedSearchRepository.existsByUserIdAndSearchName(userId, "8(a) Opportunities");
        boolean notExists = savedSearchRepository.existsByUserIdAndSearchName(userId, "Nonexistent");

        // Assert
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void shouldUpdateLastExecutedTimestamp() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Map<String, Object> criteria = Map.of("keyword", "cloud");
        SavedSearch search = new SavedSearch(userId, "Cloud Services", criteria);
        SavedSearch saved = savedSearchRepository.save(search);

        assertThat(saved.getLastExecuted()).isNull();

        // Act
        saved.setLastExecuted(java.time.Instant.now());
        SavedSearch updated = savedSearchRepository.save(saved);

        // Assert
        assertThat(updated.getLastExecuted()).isNotNull();
    }
}
