package com.athena.core.service;

import com.athena.core.dto.NaicsCreateDTO;
import com.athena.core.dto.NaicsResponseDTO;
import com.athena.core.dto.NaicsUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for NAICS entity operations.
 * Handles North American Industry Classification System codes.
 */
public interface NaicsService {

    /**
     * Create a new NAICS code.
     *
     * @param dto the NAICS creation data
     * @return the created NAICS code
     * @throws com.athena.core.exception.DuplicateEntityException if code already exists
     */
    NaicsResponseDTO create(NaicsCreateDTO dto);

    /**
     * Find NAICS code by ID.
     *
     * @param id the NAICS UUID
     * @return Optional containing the NAICS code if found
     */
    Optional<NaicsResponseDTO> findById(UUID id);

    /**
     * Find all NAICS codes with pagination.
     *
     * @param pageable pagination parameters
     * @return page of NAICS codes
     */
    Page<NaicsResponseDTO> findAll(Pageable pageable);

    /**
     * Update an existing NAICS code.
     *
     * @param id the NAICS UUID
     * @param dto the update data
     * @return the updated NAICS code
     * @throws com.athena.core.exception.EntityNotFoundException if NAICS code not found
     */
    NaicsResponseDTO update(UUID id, NaicsUpdateDTO dto);

    /**
     * Delete a NAICS code (soft delete by setting isActive to false).
     *
     * @param id the NAICS UUID
     * @throws com.athena.core.exception.EntityNotFoundException if NAICS code not found
     */
    void delete(UUID id);

    /**
     * Find NAICS code by code.
     *
     * @param code the code to search for
     * @return Optional containing the NAICS code if found
     */
    Optional<NaicsResponseDTO> findByCode(String code);

    /**
     * Check if code already exists.
     *
     * @param code the code to check
     * @return true if code exists, false otherwise
     */
    boolean existsByCode(String code);
}
