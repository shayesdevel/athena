package com.athena.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * DTO for creating a team.
 */
public record TeamCreateDTO(
    @NotNull(message = "Lead organization ID is required")
    UUID leadOrganizationId,

    @NotNull(message = "Opportunity ID is required")
    UUID opportunityId,

    @NotBlank(message = "Team name is required")
    @Size(max = 255)
    String teamName,

    @NotBlank(message = "Status is required")
    @Size(max = 50)
    String status,

    @NotNull(message = "Created by user ID is required")
    UUID createdBy
) {
}
