package com.athena.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CompetitorIntelCreateDTO(
    @NotNull(message = "Organization ID is required")
    UUID organizationId,
    @NotNull(message = "Opportunity ID is required")
    UUID opportunityId,
    @Size(max = 50)
    String likelihood,
    String strengths,
    String weaknesses,
    @NotBlank(message = "Source is required")
    @Size(max = 100)
    String source
) {}
