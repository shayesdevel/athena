package com.athena.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record OpportunityScoreCreateDTO(
    @NotNull(message = "Opportunity ID is required")
    UUID opportunityId,
    @NotBlank(message = "Score type is required")
    @Size(max = 50)
    String scoreType,
    @NotNull(message = "Score value is required")
    BigDecimal scoreValue,
    BigDecimal confidence,
    Instant scoredAt,
    Map<String, Object> metadata
) {}
