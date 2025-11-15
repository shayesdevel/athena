package com.athena.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;
import java.util.UUID;

/**
 * DTO for creating a saved search.
 */
public record SavedSearchCreateDTO(
    @NotNull(message = "User ID is required")
    UUID userId,

    @NotBlank(message = "Search name is required")
    @Size(max = 255)
    String searchName,

    @NotNull(message = "Search criteria is required")
    Map<String, Object> searchCriteria,

    Boolean isActive
) {
}
