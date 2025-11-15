package com.athena.core.dto;

import jakarta.validation.constraints.Size;

/**
 * DTO for updating an existing NAICS code.
 */
public record NaicsUpdateDTO(
    @Size(max = 500)
    String title,

    String description,

    @Size(max = 6)
    String parentCode,

    Integer level,

    @Size(max = 50)
    String yearVersion,

    Boolean isActive
) {
}
