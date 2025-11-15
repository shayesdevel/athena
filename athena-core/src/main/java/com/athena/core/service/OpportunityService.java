package com.athena.core.service;

import com.athena.core.dto.OpportunityCreateDTO;
import com.athena.core.dto.OpportunityResponseDTO;
import com.athena.core.dto.OpportunityUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for Opportunity entity operations.
 * Manages SAM.gov contract opportunities and related data.
 */
public interface OpportunityService {

    /**
     * Create a new opportunity.
     *
     * @param dto the opportunity creation data
     * @return the created opportunity
     * @throws com.athena.core.exception.DuplicateEntityException if notice ID already exists
     */
    OpportunityResponseDTO create(OpportunityCreateDTO dto);

    /**
     * Find opportunity by ID.
     *
     * @param id the opportunity UUID
     * @return Optional containing the opportunity if found
     */
    Optional<OpportunityResponseDTO> findById(UUID id);

    /**
     * Find all opportunities with pagination.
     *
     * @param pageable pagination parameters
     * @return page of opportunities
     */
    Page<OpportunityResponseDTO> findAll(Pageable pageable);

    /**
     * Update an existing opportunity.
     *
     * @param id the opportunity UUID
     * @param dto the update data
     * @return the updated opportunity
     * @throws com.athena.core.exception.EntityNotFoundException if opportunity not found
     */
    OpportunityResponseDTO update(UUID id, OpportunityUpdateDTO dto);

    /**
     * Delete an opportunity (soft delete by setting isActive to false).
     *
     * @param id the opportunity UUID
     * @throws com.athena.core.exception.EntityNotFoundException if opportunity not found
     */
    void delete(UUID id);

    /**
     * Find opportunity by notice ID.
     *
     * @param noticeId the SAM.gov notice ID
     * @return Optional containing the opportunity if found
     */
    Optional<OpportunityResponseDTO> findByNoticeId(String noticeId);

    /**
     * Find all active opportunities.
     *
     * @return List of active opportunities
     */
    List<OpportunityResponseDTO> findActiveOpportunities();

    /**
     * Find opportunities by NAICS code.
     *
     * @param naicsCode the NAICS code
     * @return List of opportunities with matching NAICS
     */
    List<OpportunityResponseDTO> findByNaicsCode(String naicsCode);

    /**
     * Find opportunities by notice type.
     *
     * @param noticeType the notice type
     * @return List of opportunities with matching notice type
     */
    List<OpportunityResponseDTO> findByNoticeType(String noticeType);

    /**
     * Find opportunities by agency.
     *
     * @param agencyId the agency UUID
     * @return List of opportunities from the agency
     */
    List<OpportunityResponseDTO> findByAgency(UUID agencyId);

    /**
     * Find opportunities posted after a specific date.
     *
     * @param date the date to search from
     * @return List of opportunities posted after the date
     */
    List<OpportunityResponseDTO> findPostedAfter(LocalDate date);

    /**
     * Find opportunities with deadline before a specific instant.
     *
     * @param deadline the deadline instant
     * @return List of opportunities expiring before the deadline
     */
    List<OpportunityResponseDTO> findExpiringBefore(Instant deadline);

    /**
     * Find active opportunities with upcoming deadlines.
     *
     * @param daysAhead number of days to look ahead
     * @return List of active opportunities with deadlines in the next N days
     */
    List<OpportunityResponseDTO> findUpcomingDeadlines(int daysAhead);

    /**
     * Search opportunities by title (partial match).
     *
     * @param title the title pattern to search for
     * @return List of matching opportunities
     */
    List<OpportunityResponseDTO> searchByTitle(String title);

    /**
     * Check if notice ID already exists.
     *
     * @param noticeId the notice ID to check
     * @return true if notice ID exists, false otherwise
     */
    boolean existsByNoticeId(String noticeId);
}
