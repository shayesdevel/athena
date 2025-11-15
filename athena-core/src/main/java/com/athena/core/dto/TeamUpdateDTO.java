package com.athena.core.dto;

import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * DTO for updating a team.
 */
public record TeamUpdateDTO(
    UUID leadOrganizationId,

    UUID opportunityId,

    @Size(max = 255)
    String teamName,

    @Size(max = 50)
    String status
) {
}
