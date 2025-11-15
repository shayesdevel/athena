package com.athena.core.repository;

import com.athena.core.entity.CompetitorIntel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for CompetitorIntel entity.
 * Provides CRUD operations and custom queries for competitive intelligence.
 */
@Repository
public interface CompetitorIntelRepository extends JpaRepository<CompetitorIntel, UUID> {

    /**
     * Find all competitor intel for a specific opportunity.
     *
     * @param opportunityId the opportunity's ID
     * @return list of competitor intel records
     */
    List<CompetitorIntel> findByOpportunityId(UUID opportunityId);

    /**
     * Find all competitor intel for a specific organization.
     *
     * @param organizationId the organization's ID
     * @return list of competitor intel records
     */
    List<CompetitorIntel> findByOrganizationId(UUID organizationId);

    /**
     * Find competitor intel by likelihood.
     *
     * @param likelihood the win likelihood rating
     * @return list of competitor intel records
     */
    List<CompetitorIntel> findByLikelihood(String likelihood);

    /**
     * Find specific competitor intel for an organization on an opportunity.
     *
     * @param organizationId the organization's ID
     * @param opportunityId the opportunity's ID
     * @return optional competitor intel record
     */
    Optional<CompetitorIntel> findByOrganizationIdAndOpportunityId(UUID organizationId, UUID opportunityId);

    /**
     * Check if competitor intel exists for an opportunity.
     *
     * @param opportunityId the opportunity's ID
     * @return true if intel exists, false otherwise
     */
    boolean existsByOpportunityId(UUID opportunityId);
}
