package com.athena.core.repository;

import com.athena.core.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for TeamMember entity.
 * Provides CRUD operations and custom queries for team members.
 */
@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, UUID> {

    /**
     * Find all members of a team.
     *
     * @param teamId the team's ID
     * @return list of team members
     */
    List<TeamMember> findByTeamId(UUID teamId);

    /**
     * Find all teams an organization is a member of.
     *
     * @param organizationId the organization's ID
     * @return list of team memberships
     */
    List<TeamMember> findByOrganizationId(UUID organizationId);

    /**
     * Find prime contractors on a team.
     *
     * @param teamId the team's ID
     * @param isPrime prime contractor status
     * @return list of prime team members
     */
    List<TeamMember> findByTeamIdAndIsPrime(UUID teamId, Boolean isPrime);

    /**
     * Find a specific team member by team and organization.
     *
     * @param teamId the team's ID
     * @param organizationId the organization's ID
     * @return optional team member
     */
    Optional<TeamMember> findByTeamIdAndOrganizationId(UUID teamId, UUID organizationId);

    /**
     * Check if an organization is a member of a team.
     *
     * @param teamId the team's ID
     * @param organizationId the organization's ID
     * @return true if member exists, false otherwise
     */
    boolean existsByTeamIdAndOrganizationId(UUID teamId, UUID organizationId);
}
