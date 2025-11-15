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
 * Alert entity representing user notification preferences and triggers.
 * Allows users to configure automated notifications for opportunities matching criteria.
 */
@Entity
@Table(name = "alerts", indexes = {
    @Index(name = "idx_alerts_user_id", columnList = "user_id"),
    @Index(name = "idx_alerts_alert_type", columnList = "alert_type"),
    @Index(name = "idx_alerts_is_active", columnList = "is_active"),
    @Index(name = "idx_alerts_last_triggered", columnList = "last_triggered")
})
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @NotBlank(message = "Alert type is required")
    @Size(max = 50)
    @Column(name = "alert_type", nullable = false, length = 50)
    private String alertType;

    @NotNull(message = "Criteria is required")
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> criteria;

    @NotBlank(message = "Frequency is required")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String frequency;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "last_triggered")
    private Instant lastTriggered;

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
        if (isActive == null) {
            isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // Constructors
    public Alert() {
    }

    public Alert(UUID userId, String alertType, Map<String, Object> criteria, String frequency) {
        this.userId = userId;
        this.alertType = alertType;
        this.criteria = criteria;
        this.frequency = frequency;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public Map<String, Object> getCriteria() {
        return criteria;
    }

    public void setCriteria(Map<String, Object> criteria) {
        this.criteria = criteria;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Instant getLastTriggered() {
        return lastTriggered;
    }

    public void setLastTriggered(Instant lastTriggered) {
        this.lastTriggered = lastTriggered;
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
        if (!(o instanceof Alert)) return false;
        Alert alert = (Alert) o;
        return id != null && id.equals(alert.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Alert{" +
                "id=" + id +
                ", userId=" + userId +
                ", alertType='" + alertType + '\'' +
                ", frequency='" + frequency + '\'' +
                ", isActive=" + isActive +
                ", lastTriggered=" + lastTriggered +
                '}';
    }
}
