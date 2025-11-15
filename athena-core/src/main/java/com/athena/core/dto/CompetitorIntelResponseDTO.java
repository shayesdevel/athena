package com.athena.core.dto;

import com.athena.core.entity.CompetitorIntel;
import java.time.Instant;
import java.util.UUID;

public record CompetitorIntelResponseDTO(
    UUID id,
    UUID organizationId,
    UUID opportunityId,
    String likelihood,
    String strengths,
    String weaknesses,
    String source,
    Instant createdAt,
    Instant updatedAt
) {
    public static CompetitorIntelResponseDTO fromEntity(CompetitorIntel competitorIntel) {
        return new CompetitorIntelResponseDTO(
            competitorIntel.getId(),
            competitorIntel.getOrganizationId(),
            competitorIntel.getOpportunityId(),
            competitorIntel.getLikelihood(),
            competitorIntel.getStrengths(),
            competitorIntel.getWeaknesses(),
            competitorIntel.getSource(),
            competitorIntel.getCreatedAt(),
            competitorIntel.getUpdatedAt()
        );
    }
}
