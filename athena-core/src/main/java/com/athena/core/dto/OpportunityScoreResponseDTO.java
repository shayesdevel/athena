package com.athena.core.dto;

import com.athena.core.entity.OpportunityScore;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record OpportunityScoreResponseDTO(
    UUID id,
    UUID opportunityId,
    String scoreType,
    BigDecimal scoreValue,
    BigDecimal confidence,
    Instant scoredAt,
    Map<String, Object> metadata,
    Instant createdAt,
    Instant updatedAt
) {
    public static OpportunityScoreResponseDTO fromEntity(OpportunityScore opportunityScore) {
        return new OpportunityScoreResponseDTO(
            opportunityScore.getId(),
            opportunityScore.getOpportunityId(),
            opportunityScore.getScoreType(),
            opportunityScore.getScoreValue(),
            opportunityScore.getConfidence(),
            opportunityScore.getScoredAt(),
            opportunityScore.getMetadata(),
            opportunityScore.getCreatedAt(),
            opportunityScore.getUpdatedAt()
        );
    }
}
