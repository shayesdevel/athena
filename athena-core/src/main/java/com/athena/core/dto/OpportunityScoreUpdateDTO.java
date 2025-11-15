package com.athena.core.dto;

import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

public record OpportunityScoreUpdateDTO(
    @Size(max = 50)
    String scoreType,
    BigDecimal scoreValue,
    BigDecimal confidence,
    Instant scoredAt,
    Map<String, Object> metadata
) {}
