package com.athena.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record TeamMemberCreateDTO(
    @NotNull(message = "Team ID is required")
    UUID teamId,
    @NotNull(message = "Organization ID is required")
    UUID organizationId,
    @NotBlank(message = "Role is required")
    @Size(max = 100)
    String role,
    String capabilities,
    Boolean isPrime,
    @NotNull(message = "Added by user ID is required")
    UUID addedBy
) {}
