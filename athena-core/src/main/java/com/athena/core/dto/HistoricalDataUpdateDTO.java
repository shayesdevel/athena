package com.athena.core.dto;

import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for updating historical data entry.
 */
public record HistoricalDataUpdateDTO(
    @Size(max = 100)
    String entityType,

    UUID entityId,

    @Size(max = 100)
    String dataType,

    Map<String, Object> dataValue,

    Instant capturedAt
) {
}
