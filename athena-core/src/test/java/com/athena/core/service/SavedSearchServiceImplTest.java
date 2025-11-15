package com.athena.core.service;

import com.athena.core.dto.SavedSearchCreateDTO;
import com.athena.core.dto.SavedSearchResponseDTO;
import com.athena.core.dto.SavedSearchUpdateDTO;
import com.athena.core.entity.SavedSearch;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.SavedSearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SavedSearchServiceImplTest {

    @Mock
    private SavedSearchRepository savedSearchRepository;

    @InjectMocks
    private SavedSearchServiceImpl savedSearchService;

    private SavedSearch testSavedSearch;
    private UUID testSavedSearchId;
    private UUID testUserId;
    private Map<String, Object> testSearchCriteria;

    @BeforeEach
    void setUp() {
        testSavedSearchId = UUID.randomUUID();
        testUserId = UUID.randomUUID();

        testSearchCriteria = new HashMap<>();
        testSearchCriteria.put("keywords", "software development");
        testSearchCriteria.put("naicsCode", "541511");
        testSearchCriteria.put("minValue", 100000);

        testSavedSearch = new SavedSearch(testUserId, "IT Services Search", testSearchCriteria);
        testSavedSearch.setId(testSavedSearchId);
        testSavedSearch.setIsActive(true);
        testSavedSearch.setCreatedAt(Instant.now());
        testSavedSearch.setUpdatedAt(Instant.now());
    }

    @Test
    void create_ShouldCreateSavedSearch_WhenValidData() {
        // Given
        SavedSearchCreateDTO dto = new SavedSearchCreateDTO(
            testUserId,
            "IT Services Search",
            testSearchCriteria,
            true
        );

        when(savedSearchRepository.save(any(SavedSearch.class))).thenReturn(testSavedSearch);

        // When
        SavedSearchResponseDTO result = savedSearchService.create(dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.searchName()).isEqualTo("IT Services Search");
        assertThat(result.userId()).isEqualTo(testUserId);
        verify(savedSearchRepository).save(any(SavedSearch.class));
    }

    @Test
    void findById_ShouldReturnSavedSearch_WhenExists() {
        // Given
        when(savedSearchRepository.findById(testSavedSearchId))
            .thenReturn(Optional.of(testSavedSearch));

        // When
        Optional<SavedSearchResponseDTO> result = savedSearchService.findById(testSavedSearchId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(testSavedSearchId);
        assertThat(result.get().searchName()).isEqualTo("IT Services Search");
        verify(savedSearchRepository).findById(testSavedSearchId);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(savedSearchRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When
        Optional<SavedSearchResponseDTO> result = savedSearchService.findById(nonExistentId);

        // Then
        assertThat(result).isEmpty();
        verify(savedSearchRepository).findById(nonExistentId);
    }

    @Test
    void findAll_ShouldReturnPageOfSavedSearches() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<SavedSearch> page = new PageImpl<>(Arrays.asList(testSavedSearch));
        when(savedSearchRepository.findAll(pageable)).thenReturn(page);

        // When
        Page<SavedSearchResponseDTO> result = savedSearchService.findAll(pageable);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).searchName()).isEqualTo("IT Services Search");
        verify(savedSearchRepository).findAll(pageable);
    }

    @Test
    void update_ShouldUpdateSavedSearch_WhenValidData() {
        // Given
        Map<String, Object> newCriteria = new HashMap<>(testSearchCriteria);
        newCriteria.put("minValue", 200000);

        SavedSearchUpdateDTO dto = new SavedSearchUpdateDTO(
            "Updated Search",
            newCriteria,
            true,
            null
        );

        when(savedSearchRepository.findById(testSavedSearchId))
            .thenReturn(Optional.of(testSavedSearch));
        when(savedSearchRepository.save(any(SavedSearch.class))).thenReturn(testSavedSearch);

        // When
        SavedSearchResponseDTO result = savedSearchService.update(testSavedSearchId, dto);

        // Then
        assertThat(result).isNotNull();
        verify(savedSearchRepository).findById(testSavedSearchId);
        verify(savedSearchRepository).save(testSavedSearch);
    }

    @Test
    void update_ShouldThrowException_WhenNotExists() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        SavedSearchUpdateDTO dto = new SavedSearchUpdateDTO(null, null, null, null);
        when(savedSearchRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> savedSearchService.update(nonExistentId, dto))
            .isInstanceOf(EntityNotFoundException.class);

        verify(savedSearchRepository).findById(nonExistentId);
        verify(savedSearchRepository, never()).save(any());
    }

    @Test
    void delete_ShouldSoftDeleteSavedSearch_WhenExists() {
        // Given
        when(savedSearchRepository.findById(testSavedSearchId))
            .thenReturn(Optional.of(testSavedSearch));
        when(savedSearchRepository.save(any(SavedSearch.class))).thenReturn(testSavedSearch);

        // When
        savedSearchService.delete(testSavedSearchId);

        // Then
        verify(savedSearchRepository).findById(testSavedSearchId);
        verify(savedSearchRepository).save(testSavedSearch);
    }

    @Test
    void delete_ShouldThrowException_WhenNotExists() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(savedSearchRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> savedSearchService.delete(nonExistentId))
            .isInstanceOf(EntityNotFoundException.class);

        verify(savedSearchRepository).findById(nonExistentId);
        verify(savedSearchRepository, never()).save(any());
    }

    @Test
    void findByUserId_ShouldReturnMatchingSavedSearches() {
        // Given
        when(savedSearchRepository.findByUserId(testUserId))
            .thenReturn(Arrays.asList(testSavedSearch));

        // When
        List<SavedSearchResponseDTO> result = savedSearchService.findByUserId(testUserId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).userId()).isEqualTo(testUserId);
        verify(savedSearchRepository).findByUserId(testUserId);
    }

    @Test
    void findActiveByUserId_ShouldReturnActiveSavedSearches() {
        // Given
        when(savedSearchRepository.findByUserIdAndIsActive(testUserId, true))
            .thenReturn(Arrays.asList(testSavedSearch));

        // When
        List<SavedSearchResponseDTO> result = savedSearchService.findActiveByUserId(testUserId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).isActive()).isTrue();
        verify(savedSearchRepository).findByUserIdAndIsActive(testUserId, true);
    }

    @Test
    void recordExecution_ShouldUpdateLastExecuted_WhenExists() {
        // Given
        when(savedSearchRepository.findById(testSavedSearchId))
            .thenReturn(Optional.of(testSavedSearch));
        when(savedSearchRepository.save(any(SavedSearch.class))).thenReturn(testSavedSearch);

        // When
        savedSearchService.recordExecution(testSavedSearchId);

        // Then
        verify(savedSearchRepository).findById(testSavedSearchId);
        verify(savedSearchRepository).save(testSavedSearch);
    }

    @Test
    void recordExecution_ShouldThrowException_WhenNotExists() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(savedSearchRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> savedSearchService.recordExecution(nonExistentId))
            .isInstanceOf(EntityNotFoundException.class);

        verify(savedSearchRepository).findById(nonExistentId);
        verify(savedSearchRepository, never()).save(any());
    }
}
