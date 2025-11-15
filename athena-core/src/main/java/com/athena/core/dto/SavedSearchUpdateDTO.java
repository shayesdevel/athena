package com.athena.core.dto;

import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.Map;

/**
 * DTO for updating a saved search.
 */
public record SavedSearchUpdateDTO(
    @Size(max = 255)
    String searchName,

    Map<String, Object> searchCriteria,

    Boolean isActive,

    Instant lastExecuted
) {
}
