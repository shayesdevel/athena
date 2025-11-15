package com.athena.core.service;

import com.athena.core.dto.SetAsideCreateDTO;
import com.athena.core.dto.SetAsideResponseDTO;
import com.athena.core.dto.SetAsideUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for SetAside entity operations.
 * Handles contract set-aside types (e.g., Small Business, 8(a), HUBZone).
 */
public interface SetAsideService {

    /**
     * Create a new set-aside.
     *
     * @param dto the set-aside creation data
     * @return the created set-aside
     * @throws com.athena.core.exception.DuplicateEntityException if code already exists
     */
    SetAsideResponseDTO create(SetAsideCreateDTO dto);

    /**
     * Find set-aside by ID.
     *
     * @param id the set-aside UUID
     * @return Optional containing the set-aside if found
     */
    Optional<SetAsideResponseDTO> findById(UUID id);

    /**
     * Find all set-asides with pagination.
     *
     * @param pageable pagination parameters
     * @return page of set-asides
     */
    Page<SetAsideResponseDTO> findAll(Pageable pageable);

    /**
     * Update an existing set-aside.
     *
     * @param id the set-aside UUID
     * @param dto the update data
     * @return the updated set-aside
     * @throws com.athena.core.exception.EntityNotFoundException if set-aside not found
     */
    SetAsideResponseDTO update(UUID id, SetAsideUpdateDTO dto);

    /**
     * Delete a set-aside (soft delete by setting isActive to false).
     *
     * @param id the set-aside UUID
     * @throws com.athena.core.exception.EntityNotFoundException if set-aside not found
     */
    void delete(UUID id);

    /**
     * Find set-aside by code.
     *
     * @param code the code to search for
     * @return Optional containing the set-aside if found
     */
    Optional<SetAsideResponseDTO> findByCode(String code);

    /**
     * Check if code already exists.
     *
     * @param code the code to check
     * @return true if code exists, false otherwise
     */
    boolean existsByCode(String code);
}
