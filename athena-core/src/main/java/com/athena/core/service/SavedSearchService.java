package com.athena.core.service;

import com.athena.core.dto.SavedSearchCreateDTO;
import com.athena.core.dto.SavedSearchResponseDTO;
import com.athena.core.dto.SavedSearchUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for SavedSearch entity operations.
 * Handles user-saved search queries for opportunities.
 */
public interface SavedSearchService {

    /**
     * Create a new saved search.
     *
     * @param dto the saved search creation data
     * @return the created saved search
     */
    SavedSearchResponseDTO create(SavedSearchCreateDTO dto);

    /**
     * Find saved search by ID.
     *
     * @param id the saved search UUID
     * @return Optional containing the saved search if found
     */
    Optional<SavedSearchResponseDTO> findById(UUID id);

    /**
     * Find all saved searches with pagination.
     *
     * @param pageable pagination parameters
     * @return page of saved searches
     */
    Page<SavedSearchResponseDTO> findAll(Pageable pageable);

    /**
     * Update an existing saved search.
     *
     * @param id the saved search UUID
     * @param dto the update data
     * @return the updated saved search
     * @throws com.athena.core.exception.EntityNotFoundException if saved search not found
     */
    SavedSearchResponseDTO update(UUID id, SavedSearchUpdateDTO dto);

    /**
     * Delete a saved search (soft delete by setting isActive to false).
     *
     * @param id the saved search UUID
     * @throws com.athena.core.exception.EntityNotFoundException if saved search not found
     */
    void delete(UUID id);

    /**
     * Find saved searches by user ID.
     *
     * @param userId the user UUID
     * @return list of saved searches for the user
     */
    List<SavedSearchResponseDTO> findByUserId(UUID userId);

    /**
     * Find active saved searches by user ID.
     *
     * @param userId the user UUID
     * @return list of active saved searches for the user
     */
    List<SavedSearchResponseDTO> findActiveByUserId(UUID userId);

    /**
     * Record execution of a saved search.
     *
     * @param id the saved search UUID
     * @throws com.athena.core.exception.EntityNotFoundException if saved search not found
     */
    void recordExecution(UUID id);
}
