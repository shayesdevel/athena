package com.athena.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;
import java.util.UUID;

/**
 * DTO for creating an alert.
 */
public record AlertCreateDTO(
    @NotNull(message = "User ID is required")
    UUID userId,

    @NotBlank(message = "Alert type is required")
    @Size(max = 50)
    String alertType,

    @NotNull(message = "Criteria is required")
    Map<String, Object> criteria,

    @NotBlank(message = "Frequency is required")
    @Size(max = 50)
    String frequency,

    Boolean isActive
) {
}
