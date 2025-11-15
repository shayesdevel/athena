package com.athena.core.dto;

import com.athena.core.entity.HistoricalData;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for historical data response.
 */
public record HistoricalDataResponseDTO(
    UUID id,
    String entityType,
    UUID entityId,
    String dataType,
    Map<String, Object> dataValue,
    Instant capturedAt,
    Instant createdAt
) {
    /**
     * Create a HistoricalDataResponseDTO from a HistoricalData entity.
     */
    public static HistoricalDataResponseDTO fromEntity(HistoricalData historicalData) {
        return new HistoricalDataResponseDTO(
            historicalData.getId(),
            historicalData.getEntityType(),
            historicalData.getEntityId(),
            historicalData.getDataType(),
            historicalData.getDataValue(),
            historicalData.getCapturedAt(),
            historicalData.getCreatedAt()
        );
    }
}
