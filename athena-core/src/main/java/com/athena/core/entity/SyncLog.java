package com.athena.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

/**
 * SyncLog entity for tracking SAM.gov synchronization operations.
 * Records sync job execution, success/failure, and error details for monitoring.
 */
@Entity
@Table(name = "sync_logs", indexes = {
    @Index(name = "idx_sync_logs_sync_type", columnList = "sync_type"),
    @Index(name = "idx_sync_logs_status", columnList = "status"),
    @Index(name = "idx_sync_logs_started_at", columnList = "started_at"),
    @Index(name = "idx_sync_logs_completed_at", columnList = "completed_at")
})
public class SyncLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Sync type is required")
    @Size(max = 100)
    @Column(name = "sync_type", nullable = false, length = 100)
    private String syncType;

    @NotNull(message = "Started at timestamp is required")
    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @NotBlank(message = "Status is required")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "records_processed")
    private Integer recordsProcessed;

    @Column(name = "error_count")
    private Integer errorCount;

    @Column(name = "error_log", columnDefinition = "TEXT")
    private String errorLog;

    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        if (startedAt == null) {
            startedAt = now;
        }
        if (recordsProcessed == null) {
            recordsProcessed = 0;
        }
        if (errorCount == null) {
            errorCount = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // Constructors
    public SyncLog() {
    }

    public SyncLog(String syncType, String status) {
        this.syncType = syncType;
        this.status = status;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSyncType() {
        return syncType;
    }

    public void setSyncType(String syncType) {
        this.syncType = syncType;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getRecordsProcessed() {
        return recordsProcessed;
    }

    public void setRecordsProcessed(Integer recordsProcessed) {
        this.recordsProcessed = recordsProcessed;
    }

    public Integer getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }

    public String getErrorLog() {
        return errorLog;
    }

    public void setErrorLog(String errorLog) {
        this.errorLog = errorLog;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SyncLog)) return false;
        SyncLog syncLog = (SyncLog) o;
        return id != null && id.equals(syncLog.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "SyncLog{" +
                "id=" + id +
                ", syncType='" + syncType + '\'' +
                ", status='" + status + '\'' +
                ", startedAt=" + startedAt +
                ", completedAt=" + completedAt +
                ", recordsProcessed=" + recordsProcessed +
                ", errorCount=" + errorCount +
                '}';
    }
}
