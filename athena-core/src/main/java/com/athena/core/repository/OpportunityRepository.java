package com.athena.core.repository;

import com.athena.core.entity.Opportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Opportunity entity operations.
 * Provides CRUD operations and custom queries for SAM.gov contract opportunity management.
 */
@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, UUID> {

    /**
     * Find opportunity by notice ID.
     *
     * @param noticeId the SAM.gov notice ID
     * @return Optional containing the opportunity if found
     */
    Optional<Opportunity> findByNoticeId(String noticeId);

    /**
     * Find active opportunities.
     *
     * @return List of active opportunities
     */
    List<Opportunity> findByIsActiveTrue();

    /**
     * Find opportunities by NAICS code.
     *
     * @param naicsCode the NAICS code to search for
     * @return List of opportunities with matching NAICS
     */
    List<Opportunity> findByNaicsCode(String naicsCode);

    /**
     * Find opportunities by notice type.
     *
     * @param noticeType the notice type to search for
     * @return List of opportunities with matching notice type
     */
    List<Opportunity> findByNoticeType(String noticeType);

    /**
     * Find opportunities by agency.
     *
     * @param agencyId the agency UUID
     * @return List of opportunities from the agency
     */
    List<Opportunity> findByAgencyId(UUID agencyId);

    /**
     * Find opportunities posted after a specific date.
     *
     * @param date the date to search from
     * @return List of opportunities posted after the date
     */
    List<Opportunity> findByPostedDateAfter(LocalDate date);

    /**
     * Find opportunities with response deadline before a specific instant.
     *
     * @param deadline the deadline instant
     * @return List of opportunities expiring before the deadline
     */
    List<Opportunity> findByResponseDeadlineBefore(Instant deadline);

    /**
     * Find active opportunities with upcoming deadlines.
     *
     * @param now the current instant
     * @param futureDeadline the future deadline instant
     * @return List of active opportunities with deadlines in range
     */
    @Query("SELECT o FROM Opportunity o WHERE o.isActive = true AND o.responseDeadline BETWEEN :now AND :futureDeadline ORDER BY o.responseDeadline ASC")
    List<Opportunity> findActiveOpportunitiesWithUpcomingDeadlines(
        @Param("now") Instant now,
        @Param("futureDeadline") Instant futureDeadline
    );

    /**
     * Find opportunities by title (case-insensitive partial match).
     *
     * @param title the title pattern to search for
     * @return List of matching opportunities
     */
    @Query("SELECT o FROM Opportunity o WHERE LOWER(o.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Opportunity> findByTitleContainingIgnoreCase(@Param("title") String title);

    /**
     * Check if notice ID already exists.
     *
     * @param noticeId the notice ID to check
     * @return true if notice ID exists, false otherwise
     */
    boolean existsByNoticeId(String noticeId);

    /**
     * Count opportunities created between start and end time.
     *
     * @param startTime start timestamp
     * @param endTime end timestamp
     * @return count of opportunities
     */
    long countByCreatedAtBetween(Instant startTime, Instant endTime);
}
