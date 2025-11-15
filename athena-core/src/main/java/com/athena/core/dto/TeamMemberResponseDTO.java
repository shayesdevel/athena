package com.athena.core.dto;

import com.athena.core.entity.TeamMember;
import java.time.Instant;
import java.util.UUID;

public record TeamMemberResponseDTO(
    UUID id,
    UUID teamId,
    UUID organizationId,
    String role,
    String capabilities,
    Boolean isPrime,
    UUID addedBy,
    Instant createdAt,
    Instant updatedAt
) {
    public static TeamMemberResponseDTO fromEntity(TeamMember teamMember) {
        return new TeamMemberResponseDTO(
            teamMember.getId(),
            teamMember.getTeamId(),
            teamMember.getOrganizationId(),
            teamMember.getRole(),
            teamMember.getCapabilities(),
            teamMember.getIsPrime(),
            teamMember.getAddedBy(),
            teamMember.getCreatedAt(),
            teamMember.getUpdatedAt()
        );
    }
}
