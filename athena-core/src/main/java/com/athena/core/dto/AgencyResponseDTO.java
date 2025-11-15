package com.athena.core.dto;

import com.athena.core.entity.Agency;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for agency response.
 */
public record AgencyResponseDTO(
    UUID id,
    String name,
    String abbreviation,
    UUID parentAgencyId,
    String department,
    String tier,
    Boolean isActive,
    Instant createdAt,
    Instant updatedAt
) {
    /**
     * Create an AgencyResponseDTO from an Agency entity.
     */
    public static AgencyResponseDTO fromEntity(Agency agency) {
        return new AgencyResponseDTO(
            agency.getId(),
            agency.getName(),
            agency.getAbbreviation(),
            agency.getParentAgency() != null ? agency.getParentAgency().getId() : null,
            agency.getDepartment(),
            agency.getTier(),
            agency.getIsActive(),
            agency.getCreatedAt(),
            agency.getUpdatedAt()
        );
    }
}
