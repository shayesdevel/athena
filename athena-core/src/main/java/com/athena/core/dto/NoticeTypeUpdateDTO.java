package com.athena.core.dto;

import jakarta.validation.constraints.Size;

/**
 * DTO for updating an existing notice type.
 */
public record NoticeTypeUpdateDTO(
    @Size(max = 255)
    String name,

    String description,

    @Size(max = 50)
    String category,

    Boolean isActive
) {
}
