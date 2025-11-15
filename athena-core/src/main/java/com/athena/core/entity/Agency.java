package com.athena.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

/**
 * Agency entity representing federal government agencies.
 * Supports hierarchical agency structure (parent-child relationships).
 */
@Entity
@Table(name = "agencies", indexes = {
    @Index(name = "idx_agencies_name", columnList = "name"),
    @Index(name = "idx_agencies_abbreviation", columnList = "abbreviation"),
    @Index(name = "idx_agencies_parent", columnList = "parent_agency_id")
})
public class Agency {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Agency name is required")
    @Size(max = 500)
    @Column(nullable = false, length = 500)
    private String name;

    @Size(max = 50)
    @Column(length = 50)
    private String abbreviation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_agency_id")
    private Agency parentAgency;

    @Size(max = 200)
    @Column(length = 200)
    private String department;

    @Size(max = 50)
    @Column(length = 50)
    private String tier;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

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
    public Agency() {
    }

    public Agency(String name) {
        this.name = name;
    }

    public Agency(String name, String abbreviation) {
        this.name = name;
        this.abbreviation = abbreviation;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public Agency getParentAgency() {
        return parentAgency;
    }

    public void setParentAgency(Agency parentAgency) {
        this.parentAgency = parentAgency;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
        if (!(o instanceof Agency)) return false;
        Agency agency = (Agency) o;
        return id != null && id.equals(agency.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Agency{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", abbreviation='" + abbreviation + '\'' +
                ", department='" + department + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
