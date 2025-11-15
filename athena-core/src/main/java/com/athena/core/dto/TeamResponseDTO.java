package com.athena.core.dto;

import com.athena.core.entity.Team;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for team response.
 */
public record TeamResponseDTO(
    UUID id,
    UUID leadOrganizationId,
    UUID opportunityId,
    String teamName,
    String status,
    UUID createdBy,
    Instant createdAt,
    Instant updatedAt
) {
    /**
     * Create a TeamResponseDTO from a Team entity.
     */
    public static TeamResponseDTO fromEntity(Team team) {
        return new TeamResponseDTO(
            team.getId(),
            team.getLeadOrganizationId(),
            team.getOpportunityId(),
            team.getTeamName(),
            team.getStatus(),
            team.getCreatedBy(),
            team.getCreatedAt(),
            team.getUpdatedAt()
        );
    }
}
