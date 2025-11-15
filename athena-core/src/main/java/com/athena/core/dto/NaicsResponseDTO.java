package com.athena.core.dto;

import com.athena.core.entity.Naics;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for NAICS response.
 */
public record NaicsResponseDTO(
    UUID id,
    String code,
    String title,
    String description,
    String parentCode,
    Integer level,
    Boolean isActive,
    String yearVersion,
    Instant createdAt,
    Instant updatedAt
) {
    /**
     * Create a NaicsResponseDTO from a Naics entity.
     */
    public static NaicsResponseDTO fromEntity(Naics naics) {
        return new NaicsResponseDTO(
            naics.getId(),
            naics.getCode(),
            naics.getTitle(),
            naics.getDescription(),
            naics.getParentCode(),
            naics.getLevel(),
            naics.getIsActive(),
            naics.getYearVersion(),
            naics.getCreatedAt(),
            naics.getUpdatedAt()
        );
    }
}
