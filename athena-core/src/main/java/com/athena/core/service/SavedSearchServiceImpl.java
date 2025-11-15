package com.athena.core.service;

import com.athena.core.dto.SavedSearchCreateDTO;
import com.athena.core.dto.SavedSearchResponseDTO;
import com.athena.core.dto.SavedSearchUpdateDTO;
import com.athena.core.entity.SavedSearch;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.SavedSearchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of SavedSearchService.
 */
@Service
@Transactional(readOnly = true)
public class SavedSearchServiceImpl implements SavedSearchService {

    private final SavedSearchRepository savedSearchRepository;

    public SavedSearchServiceImpl(SavedSearchRepository savedSearchRepository) {
        this.savedSearchRepository = savedSearchRepository;
    }

    @Override
    @Transactional
    public SavedSearchResponseDTO create(SavedSearchCreateDTO dto) {
        SavedSearch savedSearch = new SavedSearch(
            dto.userId(),
            dto.searchName(),
            dto.searchCriteria()
        );

        if (dto.isActive() != null) {
            savedSearch.setIsActive(dto.isActive());
        }

        SavedSearch saved = savedSearchRepository.save(savedSearch);
        return SavedSearchResponseDTO.fromEntity(saved);
    }

    @Override
    public Optional<SavedSearchResponseDTO> findById(UUID id) {
        return savedSearchRepository.findById(id)
            .map(SavedSearchResponseDTO::fromEntity);
    }

    @Override
    public Page<SavedSearchResponseDTO> findAll(Pageable pageable) {
        return savedSearchRepository.findAll(pageable)
            .map(SavedSearchResponseDTO::fromEntity);
    }

    @Override
    @Transactional
    public SavedSearchResponseDTO update(UUID id, SavedSearchUpdateDTO dto) {
        SavedSearch savedSearch = savedSearchRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("SavedSearch", id));

        updateEntityFromDto(dto, savedSearch);

        SavedSearch updated = savedSearchRepository.save(savedSearch);
        return SavedSearchResponseDTO.fromEntity(updated);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        SavedSearch savedSearch = savedSearchRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("SavedSearch", id));

        savedSearch.setIsActive(false);
        savedSearchRepository.save(savedSearch);
    }

    @Override
    public List<SavedSearchResponseDTO> findByUserId(UUID userId) {
        return savedSearchRepository.findByUserId(userId)
            .stream()
            .map(SavedSearchResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<SavedSearchResponseDTO> findActiveByUserId(UUID userId) {
        return savedSearchRepository.findByUserIdAndIsActive(userId, true)
            .stream()
            .map(SavedSearchResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void recordExecution(UUID id) {
        SavedSearch savedSearch = savedSearchRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("SavedSearch", id));

        savedSearch.setLastExecuted(Instant.now());
        savedSearchRepository.save(savedSearch);
    }

    /**
     * Update entity fields from update DTO (only non-null fields).
     */
    private void updateEntityFromDto(SavedSearchUpdateDTO dto, SavedSearch savedSearch) {
        if (dto.searchName() != null) savedSearch.setSearchName(dto.searchName());
        if (dto.searchCriteria() != null) savedSearch.setSearchCriteria(dto.searchCriteria());
        if (dto.isActive() != null) savedSearch.setIsActive(dto.isActive());
        if (dto.lastExecuted() != null) savedSearch.setLastExecuted(dto.lastExecuted());
    }
}
