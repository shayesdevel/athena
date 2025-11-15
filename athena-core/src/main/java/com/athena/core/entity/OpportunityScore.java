package com.athena.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * OpportunityScore entity representing AI scoring results for opportunities.
 * Tracks different score types (relevance, win probability, strategic fit) with metadata.
 */
@Entity
@Table(name = "opportunity_scores", indexes = {
    @Index(name = "idx_opportunity_scores_opportunity_id", columnList = "opportunity_id"),
    @Index(name = "idx_opportunity_scores_score_type", columnList = "score_type"),
    @Index(name = "idx_opportunity_scores_scored_at", columnList = "scored_at"),
    @Index(name = "idx_opportunity_scores_score_value", columnList = "score_value")
})
public class OpportunityScore {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Opportunity ID is required")
    @Column(name = "opportunity_id", nullable = false)
    private UUID opportunityId;

    @NotBlank(message = "Score type is required")
    @Size(max = 50)
    @Column(name = "score_type", nullable = false, length = 50)
    private String scoreType;

    @NotNull(message = "Score value is required")
    @Column(name = "score_value", nullable = false, precision = 5, scale = 2)
    private BigDecimal scoreValue;

    @Column(precision = 5, scale = 2)
    private BigDecimal confidence;

    @NotNull(message = "Scored at timestamp is required")
    @Column(name = "scored_at", nullable = false)
    private Instant scoredAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

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
        if (scoredAt == null) {
            scoredAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // Constructors
    public OpportunityScore() {
    }

    public OpportunityScore(UUID opportunityId, String scoreType, BigDecimal scoreValue) {
        this.opportunityId = opportunityId;
        this.scoreType = scoreType;
        this.scoreValue = scoreValue;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(UUID opportunityId) {
        this.opportunityId = opportunityId;
    }

    public String getScoreType() {
        return scoreType;
    }

    public void setScoreType(String scoreType) {
        this.scoreType = scoreType;
    }

    public BigDecimal getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(BigDecimal scoreValue) {
        this.scoreValue = scoreValue;
    }

    public BigDecimal getConfidence() {
        return confidence;
    }

    public void setConfidence(BigDecimal confidence) {
        this.confidence = confidence;
    }

    public Instant getScoredAt() {
        return scoredAt;
    }

    public void setScoredAt(Instant scoredAt) {
        this.scoredAt = scoredAt;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
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
        if (!(o instanceof OpportunityScore)) return false;
        OpportunityScore that = (OpportunityScore) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "OpportunityScore{" +
                "id=" + id +
                ", opportunityId=" + opportunityId +
                ", scoreType='" + scoreType + '\'' +
                ", scoreValue=" + scoreValue +
                ", confidence=" + confidence +
                ", scoredAt=" + scoredAt +
                '}';
    }
}
