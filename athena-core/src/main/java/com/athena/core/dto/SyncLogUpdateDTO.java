package com.athena.core.dto;

import jakarta.validation.constraints.Size;

import java.time.Instant;

/**
 * DTO for updating an existing sync log entry.
 */
public record SyncLogUpdateDTO(
    @Size(max = 100)
    String syncType,

    @Size(max = 50)
    String status,

    Instant completedAt,

    Integer recordsProcessed,

    Integer errorCount,

    String errorLog
) {
}
