package com.athena.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

/**
 * DTO for creating a new sync log entry.
 */
public record SyncLogCreateDTO(
    @NotBlank(message = "Sync type is required")
    @Size(max = 100)
    String syncType,

    @NotBlank(message = "Status is required")
    @Size(max = 50)
    String status,

    Instant startedAt,

    Integer recordsProcessed,

    Integer errorCount,

    String errorLog
) {
}
