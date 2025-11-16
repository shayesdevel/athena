package com.athena.core.repository;

import com.athena.core.entity.OpportunityScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
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

    /**
     * Check if a score exists for an opportunity with a specific score type.
     *
     * @param opportunityId the opportunity's ID
     * @param scoreType the score type
     * @return true if score exists, false otherwise
     */
    boolean existsByOpportunityIdAndScoreType(UUID opportunityId, String scoreType);

    /**
     * Find scores greater than or equal to threshold created after a specific time.
     *
     * @param scoreThreshold the minimum score
     * @param createdAfter the created after timestamp
     * @return list of scores
     */
    List<OpportunityScore> findByScoreValueGreaterThanEqualAndCreatedAtAfter(BigDecimal scoreThreshold, Instant createdAfter);

    /**
     * Count scores created between start and end time.
     *
     * @param startTime start timestamp
     * @param endTime end timestamp
     * @return count of scores
     */
    long countByCreatedAtBetween(Instant startTime, Instant endTime);

    /**
     * Count scores greater than or equal to threshold created between start and end time.
     *
     * @param scoreThreshold the minimum score
     * @param startTime start timestamp
     * @param endTime end timestamp
     * @return count of scores
     */
    long countByScoreValueGreaterThanEqualAndCreatedAtBetween(BigDecimal scoreThreshold, Instant startTime, Instant endTime);

    /**
     * Count scores between min and max created between start and end time.
     *
     * @param minScore minimum score
     * @param maxScore maximum score
     * @param startTime start timestamp
     * @param endTime end timestamp
     * @return count of scores
     */
    long countByScoreValueBetweenAndCreatedAtBetween(BigDecimal minScore, BigDecimal maxScore, Instant startTime, Instant endTime);

    /**
     * Count scores less than threshold created between start and end time.
     *
     * @param scoreThreshold the maximum score (exclusive)
     * @param startTime start timestamp
     * @param endTime end timestamp
     * @return count of scores
     */
    long countByScoreValueLessThanAndCreatedAtBetween(BigDecimal scoreThreshold, Instant startTime, Instant endTime);
}
