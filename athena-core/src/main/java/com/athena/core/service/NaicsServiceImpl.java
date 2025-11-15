package com.athena.core.service;

import com.athena.core.dto.NaicsCreateDTO;
import com.athena.core.dto.NaicsResponseDTO;
import com.athena.core.dto.NaicsUpdateDTO;
import com.athena.core.entity.Naics;
import com.athena.core.exception.DuplicateEntityException;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.NaicsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of NaicsService.
 */
@Service
@Transactional(readOnly = true)
public class NaicsServiceImpl implements NaicsService {

    private final NaicsRepository naicsRepository;

    public NaicsServiceImpl(NaicsRepository naicsRepository) {
        this.naicsRepository = naicsRepository;
    }

    @Override
    @Transactional
    public NaicsResponseDTO create(NaicsCreateDTO dto) {
        // Check for duplicates
        if (naicsRepository.existsByCode(dto.code())) {
            throw new DuplicateEntityException("Naics", "code", dto.code());
        }

        // Create NAICS entity
        Naics naics = new Naics(dto.code(), dto.title());
        naics.setDescription(dto.description());
        naics.setParentCode(dto.parentCode());
        naics.setLevel(dto.level());
        naics.setYearVersion(dto.yearVersion());

        // Save and return
        Naics savedNaics = naicsRepository.save(naics);
        return NaicsResponseDTO.fromEntity(savedNaics);
    }

    @Override
    public Optional<NaicsResponseDTO> findById(UUID id) {
        return naicsRepository.findById(id)
            .map(NaicsResponseDTO::fromEntity);
    }

    @Override
    public Page<NaicsResponseDTO> findAll(Pageable pageable) {
        return naicsRepository.findAll(pageable)
            .map(NaicsResponseDTO::fromEntity);
    }

    @Override
    @Transactional
    public NaicsResponseDTO update(UUID id, NaicsUpdateDTO dto) {
        Naics naics = naicsRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Naics", id));

        // Update fields if provided
        if (dto.title() != null) {
            naics.setTitle(dto.title());
        }

        if (dto.description() != null) {
            naics.setDescription(dto.description());
        }

        if (dto.parentCode() != null) {
            naics.setParentCode(dto.parentCode());
        }

        if (dto.level() != null) {
            naics.setLevel(dto.level());
        }

        if (dto.yearVersion() != null) {
            naics.setYearVersion(dto.yearVersion());
        }

        if (dto.isActive() != null) {
            naics.setIsActive(dto.isActive());
        }

        Naics updatedNaics = naicsRepository.save(naics);
        return NaicsResponseDTO.fromEntity(updatedNaics);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Naics naics = naicsRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Naics", id));

        // Soft delete
        naics.setIsActive(false);
        naicsRepository.save(naics);
    }

    @Override
    public Optional<NaicsResponseDTO> findByCode(String code) {
        return naicsRepository.findByCode(code)
            .map(NaicsResponseDTO::fromEntity);
    }

    @Override
    public boolean existsByCode(String code) {
        return naicsRepository.existsByCode(code);
    }
}
