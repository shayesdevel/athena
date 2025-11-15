package com.athena.core.dto;

import com.athena.core.entity.SavedSearch;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for saved search response.
 */
public record SavedSearchResponseDTO(
    UUID id,
    UUID userId,
    String searchName,
    Map<String, Object> searchCriteria,
    Boolean isActive,
    Instant lastExecuted,
    Instant createdAt,
    Instant updatedAt
) {
    /**
     * Create a SavedSearchResponseDTO from a SavedSearch entity.
     */
    public static SavedSearchResponseDTO fromEntity(SavedSearch savedSearch) {
        return new SavedSearchResponseDTO(
            savedSearch.getId(),
            savedSearch.getUserId(),
            savedSearch.getSearchName(),
            savedSearch.getSearchCriteria(),
            savedSearch.getIsActive(),
            savedSearch.getLastExecuted(),
            savedSearch.getCreatedAt(),
            savedSearch.getUpdatedAt()
        );
    }
}
