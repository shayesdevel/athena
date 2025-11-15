package com.athena.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

/**
 * Team entity representing contractor teaming arrangements.
 * Tracks teams formed to pursue specific opportunities.
 */
@Entity
@Table(name = "teams", indexes = {
    @Index(name = "idx_teams_lead_organization_id", columnList = "lead_organization_id"),
    @Index(name = "idx_teams_opportunity_id", columnList = "opportunity_id"),
    @Index(name = "idx_teams_created_by", columnList = "created_by"),
    @Index(name = "idx_teams_status", columnList = "status")
})
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Lead organization ID is required")
    @Column(name = "lead_organization_id", nullable = false)
    private UUID leadOrganizationId;

    @NotNull(message = "Opportunity ID is required")
    @Column(name = "opportunity_id", nullable = false)
    private UUID opportunityId;

    @NotBlank(message = "Team name is required")
    @Size(max = 255)
    @Column(name = "team_name", nullable = false, length = 255)
    private String teamName;

    @NotBlank(message = "Status is required")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String status;

    @NotNull(message = "Created by user ID is required")
    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

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
    public Team() {
    }

    public Team(UUID leadOrganizationId, UUID opportunityId, String teamName, String status, UUID createdBy) {
        this.leadOrganizationId = leadOrganizationId;
        this.opportunityId = opportunityId;
        this.teamName = teamName;
        this.status = status;
        this.createdBy = createdBy;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getLeadOrganizationId() {
        return leadOrganizationId;
    }

    public void setLeadOrganizationId(UUID leadOrganizationId) {
        this.leadOrganizationId = leadOrganizationId;
    }

    public UUID getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(UUID opportunityId) {
        this.opportunityId = opportunityId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
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
        if (!(o instanceof Team)) return false;
        Team team = (Team) o;
        return id != null && id.equals(team.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", leadOrganizationId=" + leadOrganizationId +
                ", opportunityId=" + opportunityId +
                ", teamName='" + teamName + '\'' +
                ", status='" + status + '\'' +
                ", createdBy=" + createdBy +
                '}';
    }
}
