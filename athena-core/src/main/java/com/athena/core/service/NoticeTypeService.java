package com.athena.core.service;

import com.athena.core.dto.NoticeTypeCreateDTO;
import com.athena.core.dto.NoticeTypeResponseDTO;
import com.athena.core.dto.NoticeTypeUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for NoticeType entity operations.
 * Handles SAM.gov notice type management (e.g., Presolicitation, Award Notice).
 */
public interface NoticeTypeService {

    /**
     * Create a new notice type.
     *
     * @param dto the notice type creation data
     * @return the created notice type
     * @throws com.athena.core.exception.DuplicateEntityException if code already exists
     */
    NoticeTypeResponseDTO create(NoticeTypeCreateDTO dto);

    /**
     * Find notice type by ID.
     *
     * @param id the notice type UUID
     * @return Optional containing the notice type if found
     */
    Optional<NoticeTypeResponseDTO> findById(UUID id);

    /**
     * Find all notice types with pagination.
     *
     * @param pageable pagination parameters
     * @return page of notice types
     */
    Page<NoticeTypeResponseDTO> findAll(Pageable pageable);

    /**
     * Update an existing notice type.
     *
     * @param id the notice type UUID
     * @param dto the update data
     * @return the updated notice type
     * @throws com.athena.core.exception.EntityNotFoundException if notice type not found
     */
    NoticeTypeResponseDTO update(UUID id, NoticeTypeUpdateDTO dto);

    /**
     * Delete a notice type (soft delete by setting isActive to false).
     *
     * @param id the notice type UUID
     * @throws com.athena.core.exception.EntityNotFoundException if notice type not found
     */
    void delete(UUID id);

    /**
     * Find notice type by code.
     *
     * @param code the code to search for
     * @return Optional containing the notice type if found
     */
    Optional<NoticeTypeResponseDTO> findByCode(String code);

    /**
     * Check if code already exists.
     *
     * @param code the code to check
     * @return true if code exists, false otherwise
     */
    boolean existsByCode(String code);
}
