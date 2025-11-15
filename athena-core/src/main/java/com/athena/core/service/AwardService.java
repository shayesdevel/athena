package com.athena.core.service;

import com.athena.core.dto.AwardCreateDTO;
import com.athena.core.dto.AwardResponseDTO;
import com.athena.core.dto.AwardUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for Award entity operations.
 * Handles contract award data management and tracking.
 */
public interface AwardService {

    /**
     * Create a new award.
     *
     * @param dto the award creation data
     * @return the created award
     * @throws com.athena.core.exception.DuplicateEntityException if contract number already exists
     */
    AwardResponseDTO create(AwardCreateDTO dto);

    /**
     * Find award by ID.
     *
     * @param id the award UUID
     * @return Optional containing the award if found
     */
    Optional<AwardResponseDTO> findById(UUID id);

    /**
     * Find all awards with pagination.
     *
     * @param pageable pagination parameters
     * @return page of awards
     */
    Page<AwardResponseDTO> findAll(Pageable pageable);

    /**
     * Update an existing award.
     *
     * @param id the award UUID
     * @param dto the update data
     * @return the updated award
     * @throws com.athena.core.exception.EntityNotFoundException if award not found
     */
    AwardResponseDTO update(UUID id, AwardUpdateDTO dto);

    /**
     * Delete an award (soft delete by setting isActive to false).
     *
     * @param id the award UUID
     * @throws com.athena.core.exception.EntityNotFoundException if award not found
     */
    void delete(UUID id);

    /**
     * Find award by contract number.
     *
     * @param contractNumber the contract number to search for
     * @return Optional containing the award if found
     */
    Optional<AwardResponseDTO> findByContractNumber(String contractNumber);

    /**
     * Find all active awards.
     *
     * @return list of active awards
     */
    List<AwardResponseDTO> findActiveAwards();

    /**
     * Find awards by date range.
     *
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return list of awards within the date range
     */
    List<AwardResponseDTO> findByAwardDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find awards by NAICS code.
     *
     * @param naicsCode the NAICS code
     * @return list of awards in the specified NAICS code
     */
    List<AwardResponseDTO> findByNaicsCode(String naicsCode);

    /**
     * Find awards by awardee UEI.
     *
     * @param awardeeUei the awardee UEI
     * @return list of awards for the UEI
     */
    List<AwardResponseDTO> findByAwardeeUei(String awardeeUei);

    /**
     * Check if contract number already exists.
     *
     * @param contractNumber the contract number to check
     * @return true if exists, false otherwise
     */
    boolean existsByContractNumber(String contractNumber);
}
