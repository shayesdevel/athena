package com.athena.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

/**
 * CompetitorIntel entity representing competitive analysis for opportunities.
 * Tracks competitor organizations, win likelihood, and competitive intelligence.
 */
@Entity
@Table(name = "competitor_intel", indexes = {
    @Index(name = "idx_competitor_intel_organization_id", columnList = "organization_id"),
    @Index(name = "idx_competitor_intel_opportunity_id", columnList = "opportunity_id"),
    @Index(name = "idx_competitor_intel_likelihood", columnList = "likelihood"),
    @Index(name = "idx_competitor_intel_source", columnList = "source")
})
public class CompetitorIntel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Organization ID is required")
    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @NotNull(message = "Opportunity ID is required")
    @Column(name = "opportunity_id", nullable = false)
    private UUID opportunityId;

    @Size(max = 50)
    @Column(length = 50)
    private String likelihood;

    @Column(columnDefinition = "TEXT")
    private String strengths;

    @Column(columnDefinition = "TEXT")
    private String weaknesses;

    @NotBlank(message = "Source is required")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String source;

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
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // Constructors
    public CompetitorIntel() {
    }

    public CompetitorIntel(UUID organizationId, UUID opportunityId, String source) {
        this.organizationId = organizationId;
        this.opportunityId = opportunityId;
        this.source = source;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public UUID getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(UUID opportunityId) {
        this.opportunityId = opportunityId;
    }

    public String getLikelihood() {
        return likelihood;
    }

    public void setLikelihood(String likelihood) {
        this.likelihood = likelihood;
    }

    public String getStrengths() {
        return strengths;
    }

    public void setStrengths(String strengths) {
        this.strengths = strengths;
    }

    public String getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(String weaknesses) {
        this.weaknesses = weaknesses;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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
        if (!(o instanceof CompetitorIntel)) return false;
        CompetitorIntel that = (CompetitorIntel) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "CompetitorIntel{" +
                "id=" + id +
                ", organizationId=" + organizationId +
                ", opportunityId=" + opportunityId +
                ", likelihood='" + likelihood + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}
