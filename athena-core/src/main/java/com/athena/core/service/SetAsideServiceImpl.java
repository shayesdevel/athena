package com.athena.core.service;

import com.athena.core.dto.SetAsideCreateDTO;
import com.athena.core.dto.SetAsideResponseDTO;
import com.athena.core.dto.SetAsideUpdateDTO;
import com.athena.core.entity.SetAside;
import com.athena.core.exception.DuplicateEntityException;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.SetAsideRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of SetAsideService.
 */
@Service
@Transactional(readOnly = true)
public class SetAsideServiceImpl implements SetAsideService {

    private final SetAsideRepository setAsideRepository;

    public SetAsideServiceImpl(SetAsideRepository setAsideRepository) {
        this.setAsideRepository = setAsideRepository;
    }

    @Override
    @Transactional
    public SetAsideResponseDTO create(SetAsideCreateDTO dto) {
        // Check for duplicates
        if (setAsideRepository.existsByCode(dto.code())) {
            throw new DuplicateEntityException("SetAside", "code", dto.code());
        }

        // Create set-aside entity
        SetAside setAside = new SetAside(dto.code(), dto.name());
        setAside.setDescription(dto.description());
        setAside.setEligibilityCriteria(dto.eligibilityCriteria());

        // Save and return
        SetAside savedSetAside = setAsideRepository.save(setAside);
        return SetAsideResponseDTO.fromEntity(savedSetAside);
    }

    @Override
    public Optional<SetAsideResponseDTO> findById(UUID id) {
        return setAsideRepository.findById(id)
            .map(SetAsideResponseDTO::fromEntity);
    }

    @Override
    public Page<SetAsideResponseDTO> findAll(Pageable pageable) {
        return setAsideRepository.findAll(pageable)
            .map(SetAsideResponseDTO::fromEntity);
    }

    @Override
    @Transactional
    public SetAsideResponseDTO update(UUID id, SetAsideUpdateDTO dto) {
        SetAside setAside = setAsideRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("SetAside", id));

        // Update fields if provided
        if (dto.name() != null) {
            setAside.setName(dto.name());
        }

        if (dto.description() != null) {
            setAside.setDescription(dto.description());
        }

        if (dto.eligibilityCriteria() != null) {
            setAside.setEligibilityCriteria(dto.eligibilityCriteria());
        }

        if (dto.isActive() != null) {
            setAside.setIsActive(dto.isActive());
        }

        SetAside updatedSetAside = setAsideRepository.save(setAside);
        return SetAsideResponseDTO.fromEntity(updatedSetAside);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        SetAside setAside = setAsideRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("SetAside", id));

        // Soft delete
        setAside.setIsActive(false);
        setAsideRepository.save(setAside);
    }

    @Override
    public Optional<SetAsideResponseDTO> findByCode(String code) {
        return setAsideRepository.findByCode(code)
            .map(SetAsideResponseDTO::fromEntity);
    }

    @Override
    public boolean existsByCode(String code) {
        return setAsideRepository.existsByCode(code);
    }
}
