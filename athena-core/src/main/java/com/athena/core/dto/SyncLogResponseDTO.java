package com.athena.core.dto;

import com.athena.core.entity.SyncLog;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for sync log response.
 */
public record SyncLogResponseDTO(
    UUID id,
    String syncType,
    Instant startedAt,
    Instant completedAt,
    String status,
    Integer recordsProcessed,
    Integer errorCount,
    String errorLog,
    Instant createdAt,
    Instant updatedAt
) {
    /**
     * Create a SyncLogResponseDTO from a SyncLog entity.
     */
    public static SyncLogResponseDTO fromEntity(SyncLog syncLog) {
        return new SyncLogResponseDTO(
            syncLog.getId(),
            syncLog.getSyncType(),
            syncLog.getStartedAt(),
            syncLog.getCompletedAt(),
            syncLog.getStatus(),
            syncLog.getRecordsProcessed(),
            syncLog.getErrorCount(),
            syncLog.getErrorLog(),
            syncLog.getCreatedAt(),
            syncLog.getUpdatedAt()
        );
    }
}
