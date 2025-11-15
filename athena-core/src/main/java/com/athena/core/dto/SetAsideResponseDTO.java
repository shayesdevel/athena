package com.athena.core.dto;

import com.athena.core.entity.SetAside;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for set-aside response.
 */
public record SetAsideResponseDTO(
    UUID id,
    String code,
    String name,
    String description,
    Boolean isActive,
    String eligibilityCriteria,
    Instant createdAt,
    Instant updatedAt
) {
    /**
     * Create a SetAsideResponseDTO from a SetAside entity.
     */
    public static SetAsideResponseDTO fromEntity(SetAside setAside) {
        return new SetAsideResponseDTO(
            setAside.getId(),
            setAside.getCode(),
            setAside.getName(),
            setAside.getDescription(),
            setAside.getIsActive(),
            setAside.getEligibilityCriteria(),
            setAside.getCreatedAt(),
            setAside.getUpdatedAt()
        );
    }
}
