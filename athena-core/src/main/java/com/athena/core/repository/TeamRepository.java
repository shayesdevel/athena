package com.athena.core.repository;

import com.athena.core.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Team entity.
 * Provides CRUD operations and custom queries for contractor teams.
 */
@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {

    /**
     * Find teams by lead organization ID.
     *
     * @param leadOrganizationId the lead organization's ID
     * @return list of teams
     */
    List<Team> findByLeadOrganizationId(UUID leadOrganizationId);

    /**
     * Find teams by opportunity ID.
     *
     * @param opportunityId the opportunity's ID
     * @return list of teams
     */
    List<Team> findByOpportunityId(UUID opportunityId);

    /**
     * Find teams by status.
     *
     * @param status the team status
     * @return list of teams
     */
    List<Team> findByStatus(String status);

    /**
     * Find teams created by a specific user.
     *
     * @param createdBy the creator's user ID
     * @return list of teams
     */
    List<Team> findByCreatedBy(UUID createdBy);

    /**
     * Find a team by lead organization and opportunity.
     *
     * @param leadOrganizationId the lead organization's ID
     * @param opportunityId the opportunity's ID
     * @return optional team
     */
    Optional<Team> findByLeadOrganizationIdAndOpportunityId(UUID leadOrganizationId, UUID opportunityId);

    /**
     * Check if a team exists for an opportunity.
     *
     * @param opportunityId the opportunity's ID
     * @return true if team exists, false otherwise
     */
    boolean existsByOpportunityId(UUID opportunityId);
}
