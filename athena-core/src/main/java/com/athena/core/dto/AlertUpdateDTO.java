package com.athena.core.dto;

import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.Map;

/**
 * DTO for updating an alert.
 */
public record AlertUpdateDTO(
    @Size(max = 50)
    String alertType,

    Map<String, Object> criteria,

    @Size(max = 50)
    String frequency,

    Boolean isActive,

    Instant lastTriggered
) {
}
