package com.athena.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

/**
 * TeamMember entity representing individual members of a contractor team.
 * Links organizations to teams with role and capability information.
 */
@Entity
@Table(name = "team_members", indexes = {
    @Index(name = "idx_team_members_team_id", columnList = "team_id"),
    @Index(name = "idx_team_members_organization_id", columnList = "organization_id"),
    @Index(name = "idx_team_members_added_by", columnList = "added_by"),
    @Index(name = "idx_team_members_is_prime", columnList = "is_prime")
})
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Team ID is required")
    @Column(name = "team_id", nullable = false)
    private UUID teamId;

    @NotNull(message = "Organization ID is required")
    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @NotBlank(message = "Role is required")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String role;

    @Column(columnDefinition = "TEXT")
    private String capabilities;

    @NotNull
    @Column(name = "is_prime", nullable = false)
    private Boolean isPrime = false;

    @NotNull(message = "Added by user ID is required")
    @Column(name = "added_by", nullable = false)
    private UUID addedBy;

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
        if (isPrime == null) {
            isPrime = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // Constructors
    public TeamMember() {
    }

    public TeamMember(UUID teamId, UUID organizationId, String role, UUID addedBy) {
        this.teamId = teamId;
        this.organizationId = organizationId;
        this.role = role;
        this.addedBy = addedBy;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTeamId() {
        return teamId;
    }

    public void setTeamId(UUID teamId) {
        this.teamId = teamId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public Boolean getIsPrime() {
        return isPrime;
    }

    public void setIsPrime(Boolean isPrime) {
        this.isPrime = isPrime;
    }

    public UUID getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(UUID addedBy) {
        this.addedBy = addedBy;
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
        if (!(o instanceof TeamMember)) return false;
        TeamMember that = (TeamMember) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "TeamMember{" +
                "id=" + id +
                ", teamId=" + teamId +
                ", organizationId=" + organizationId +
                ", role='" + role + '\'' +
                ", isPrime=" + isPrime +
                ", addedBy=" + addedBy +
                '}';
    }
}
