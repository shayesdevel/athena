package com.athena.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

/**
 * NAICS entity representing North American Industry Classification System codes.
 * Used to classify industries and determine business eligibility for contracts.
 * NAICS codes are 2-6 digit hierarchical codes (e.g., "541512" for Computer Systems Design Services).
 */
@Entity
@Table(name = "naics", indexes = {
    @Index(name = "idx_naics_code", columnList = "code"),
    @Index(name = "idx_naics_parent_code", columnList = "parent_code"),
    @Index(name = "idx_naics_is_active", columnList = "is_active")
})
public class Naics {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "NAICS code is required")
    @Size(max = 6)
    @Column(nullable = false, unique = true, length = 6)
    private String code;

    @NotBlank(message = "NAICS title is required")
    @Size(max = 500)
    @Column(nullable = false, length = 500)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Size(max = 6)
    @Column(name = "parent_code", length = 6)
    private String parentCode;

    @NotNull
    @Column(name = "level", nullable = false)
    private Integer level; // 2, 3, 4, 5, or 6 digit level

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Size(max = 50)
    @Column(name = "year_version", length = 50)
    private String yearVersion; // e.g., "2022", "2017"

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
        // Auto-calculate level from code length if not set
        if (level == null && code != null) {
            level = code.length();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // Constructors
    public Naics() {
    }

    public Naics(String code, String title) {
        this.code = code;
        this.title = title;
        this.level = code != null ? code.length() : null;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
        // Auto-update level when code changes
        if (code != null) {
            this.level = code.length();
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getYearVersion() {
        return yearVersion;
    }

    public void setYearVersion(String yearVersion) {
        this.yearVersion = yearVersion;
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
        if (!(o instanceof Naics)) return false;
        Naics naics = (Naics) o;
        return id != null && id.equals(naics.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Naics{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", title='" + title + '\'' +
                ", level=" + level +
                ", isActive=" + isActive +
                '}';
    }
}
