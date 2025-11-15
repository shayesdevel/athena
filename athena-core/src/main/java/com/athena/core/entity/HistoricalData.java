package com.athena.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * HistoricalData entity for capturing historical snapshots and trends.
 * Generic entity for storing time-series data for analytics and historical tracking.
 */
@Entity
@Table(name = "historical_data", indexes = {
    @Index(name = "idx_historical_data_entity_type", columnList = "entity_type"),
    @Index(name = "idx_historical_data_entity_id", columnList = "entity_id"),
    @Index(name = "idx_historical_data_data_type", columnList = "data_type"),
    @Index(name = "idx_historical_data_captured_at", columnList = "captured_at")
})
public class HistoricalData {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Entity type is required")
    @Size(max = 100)
    @Column(name = "entity_type", nullable = false, length = 100)
    private String entityType;

    @NotNull(message = "Entity ID is required")
    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @NotBlank(message = "Data type is required")
    @Size(max = 100)
    @Column(name = "data_type", nullable = false, length = 100)
    private String dataType;

    @NotNull(message = "Data value is required")
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data_value", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> dataValue;

    @NotNull(message = "Captured at timestamp is required")
    @Column(name = "captured_at", nullable = false)
    private Instant capturedAt;

    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        if (capturedAt == null) {
            capturedAt = createdAt;
        }
    }

    // Constructors
    public HistoricalData() {
    }

    public HistoricalData(String entityType, UUID entityId, String dataType, Map<String, Object> dataValue) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.dataType = dataType;
        this.dataValue = dataValue;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Map<String, Object> getDataValue() {
        return dataValue;
    }

    public void setDataValue(Map<String, Object> dataValue) {
        this.dataValue = dataValue;
    }

    public Instant getCapturedAt() {
        return capturedAt;
    }

    public void setCapturedAt(Instant capturedAt) {
        this.capturedAt = capturedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HistoricalData)) return false;
        HistoricalData that = (HistoricalData) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "HistoricalData{" +
                "id=" + id +
                ", entityType='" + entityType + '\'' +
                ", entityId=" + entityId +
                ", dataType='" + dataType + '\'' +
                ", capturedAt=" + capturedAt +
                '}';
    }
}
