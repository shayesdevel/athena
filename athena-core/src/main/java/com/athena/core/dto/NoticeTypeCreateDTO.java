package com.athena.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a new notice type.
 */
public record NoticeTypeCreateDTO(
    @NotBlank(message = "Notice type code is required")
    @Size(max = 50)
    String code,

    @NotBlank(message = "Notice type name is required")
    @Size(max = 255)
    String name,

    String description,

    @Size(max = 50)
    String category
) {
}
