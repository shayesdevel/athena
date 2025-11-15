package com.athena.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a new NAICS code.
 */
public record NaicsCreateDTO(
    @NotBlank(message = "NAICS code is required")
    @Size(max = 6)
    String code,

    @NotBlank(message = "NAICS title is required")
    @Size(max = 500)
    String title,

    String description,

    @Size(max = 6)
    String parentCode,

    @NotNull(message = "Level is required")
    Integer level,

    @Size(max = 50)
    String yearVersion
) {
}
