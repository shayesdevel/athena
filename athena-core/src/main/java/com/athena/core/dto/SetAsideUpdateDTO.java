package com.athena.core.dto;

import jakarta.validation.constraints.Size;

/**
 * DTO for updating an existing set-aside.
 */
public record SetAsideUpdateDTO(
    @Size(max = 255)
    String name,

    String description,

    @Size(max = 100)
    String eligibilityCriteria,

    Boolean isActive
) {
}
