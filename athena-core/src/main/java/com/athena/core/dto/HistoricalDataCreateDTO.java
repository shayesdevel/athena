package com.athena.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for creating historical data entry.
 */
public record HistoricalDataCreateDTO(
    @NotBlank(message = "Entity type is required")
    @Size(max = 100)
    String entityType,

    @NotNull(message = "Entity ID is required")
    UUID entityId,

    @NotBlank(message = "Data type is required")
    @Size(max = 100)
    String dataType,

    @NotNull(message = "Data value is required")
    Map<String, Object> dataValue,

    Instant capturedAt
) {
}
