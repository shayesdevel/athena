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
 * SavedSearch entity representing user-saved search queries.
 * Allows users to save and re-execute common search criteria.
 */
@Entity
@Table(name = "saved_searches", indexes = {
    @Index(name = "idx_saved_searches_user_id", columnList = "user_id"),
    @Index(name = "idx_saved_searches_is_active", columnList = "is_active"),
    @Index(name = "idx_saved_searches_last_executed", columnList = "last_executed")
})
public class SavedSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @NotBlank(message = "Search name is required")
    @Size(max = 255)
    @Column(name = "search_name", nullable = false, length = 255)
    private String searchName;

    @NotNull(message = "Search criteria is required")
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "search_criteria", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> searchCriteria;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "last_executed")
    private Instant lastExecuted;

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
    public SavedSearch() {
    }

    public SavedSearch(UUID userId, String searchName, Map<String, Object> searchCriteria) {
        this.userId = userId;
        this.searchName = searchName;
        this.searchCriteria = searchCriteria;
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

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public Map<String, Object> getSearchCriteria() {
        return searchCriteria;
    }

    public void setSearchCriteria(Map<String, Object> searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Instant getLastExecuted() {
        return lastExecuted;
    }

    public void setLastExecuted(Instant lastExecuted) {
        this.lastExecuted = lastExecuted;
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
        if (!(o instanceof SavedSearch)) return false;
        SavedSearch that = (SavedSearch) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "SavedSearch{" +
                "id=" + id +
                ", userId=" + userId +
                ", searchName='" + searchName + '\'' +
                ", isActive=" + isActive +
                ", lastExecuted=" + lastExecuted +
                ", createdAt=" + createdAt +
                '}';
    }
}
