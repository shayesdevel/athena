package com.athena.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a new set-aside.
 */
public record SetAsideCreateDTO(
    @NotBlank(message = "Set-aside code is required")
    @Size(max = 50)
    String code,

    @NotBlank(message = "Set-aside name is required")
    @Size(max = 255)
    String name,

    String description,

    @Size(max = 100)
    String eligibilityCriteria
) {
}
