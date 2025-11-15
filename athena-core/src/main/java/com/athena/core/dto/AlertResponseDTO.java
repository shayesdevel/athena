package com.athena.core.dto;

import com.athena.core.entity.Alert;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for alert response.
 */
public record AlertResponseDTO(
    UUID id,
    UUID userId,
    String alertType,
    Map<String, Object> criteria,
    String frequency,
    Boolean isActive,
    Instant lastTriggered,
    Instant createdAt,
    Instant updatedAt
) {
    /**
     * Create an AlertResponseDTO from an Alert entity.
     */
    public static AlertResponseDTO fromEntity(Alert alert) {
        return new AlertResponseDTO(
            alert.getId(),
            alert.getUserId(),
            alert.getAlertType(),
            alert.getCriteria(),
            alert.getFrequency(),
            alert.getIsActive(),
            alert.getLastTriggered(),
            alert.getCreatedAt(),
            alert.getUpdatedAt()
        );
    }
}
