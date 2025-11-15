package com.athena.core.repository;

import com.athena.core.entity.OpportunityScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for OpportunityScore entity.
 * Provides CRUD operations and custom queries for opportunity scores.
 */
@Repository
public interface OpportunityScoreRepository extends JpaRepository<OpportunityScore, UUID> {

    /**
     * Find all scores for a specific opportunity.
     *
     * @param opportunityId the opportunity's ID
     * @return list of scores
     */
    List<OpportunityScore> findByOpportunityId(UUID opportunityId);

    /**
     * Find scores by opportunity ID and score type.
     *
     * @param opportunityId the opportunity's ID
     * @param scoreType the score type
     * @return list of scores
     */
    List<OpportunityScore> findByOpportunityIdAndScoreType(UUID opportunityId, String scoreType);

    /**
     * Find the latest score for an opportunity by type.
     *
     * @param opportunityId the opportunity's ID
     * @param scoreType the score type
     * @return optional latest score
     */
    @Query("SELECT os FROM OpportunityScore os WHERE os.opportunityId = :opportunityId " +
           "AND os.scoreType = :scoreType ORDER BY os.scoredAt DESC LIMIT 1")
    Optional<OpportunityScore> findLatestByOpportunityIdAndScoreType(
            @Param("opportunityId") UUID opportunityId,
            @Param("scoreType") String scoreType);

    /**
     * Find all scores of a specific type.
     *
     * @param scoreType the score type
     * @return list of scores
     */
    List<OpportunityScore> findByScoreType(String scoreType);

    /**
     * Check if scores exist for an opportunity.
     *
     * @param opportunityId the opportunity's ID
     * @return true if scores exist, false otherwise
     */
    boolean existsByOpportunityId(UUID opportunityId);
}
