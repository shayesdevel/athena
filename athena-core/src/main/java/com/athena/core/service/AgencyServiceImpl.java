package com.athena.core.service;

import com.athena.core.dto.AgencyCreateDTO;
import com.athena.core.dto.AgencyResponseDTO;
import com.athena.core.dto.AgencyUpdateDTO;
import com.athena.core.entity.Agency;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.AgencyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of AgencyService.
 */
@Service
@Transactional(readOnly = true)
public class AgencyServiceImpl implements AgencyService {

    private final AgencyRepository agencyRepository;

    public AgencyServiceImpl(AgencyRepository agencyRepository) {
        this.agencyRepository = agencyRepository;
    }

    @Override
    @Transactional
    public AgencyResponseDTO create(AgencyCreateDTO dto) {
        Agency agency = new Agency(dto.name(), dto.abbreviation());

        // Set parent agency if provided
        if (dto.parentAgencyId() != null) {
            Agency parentAgency = agencyRepository.findById(dto.parentAgencyId())
                .orElseThrow(() -> new EntityNotFoundException("Parent Agency", dto.parentAgencyId()));
            agency.setParentAgency(parentAgency);
        }

        agency.setDepartment(dto.department());
        agency.setTier(dto.tier());
        agency.setIsActive(dto.isActive() != null ? dto.isActive() : true);

        Agency savedAgency = agencyRepository.save(agency);
        return AgencyResponseDTO.fromEntity(savedAgency);
    }

    @Override
    public Optional<AgencyResponseDTO> findById(UUID id) {
        return agencyRepository.findById(id)
            .map(AgencyResponseDTO::fromEntity);
    }

    @Override
    public Page<AgencyResponseDTO> findAll(Pageable pageable) {
        return agencyRepository.findAll(pageable)
            .map(AgencyResponseDTO::fromEntity);
    }

    @Override
    @Transactional
    public AgencyResponseDTO update(UUID id, AgencyUpdateDTO dto) {
        Agency agency = agencyRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Agency", id));

        // Update fields if provided
        if (dto.name() != null) {
            agency.setName(dto.name());
        }

        if (dto.abbreviation() != null) {
            agency.setAbbreviation(dto.abbreviation());
        }

        if (dto.parentAgencyId() != null) {
            Agency parentAgency = agencyRepository.findById(dto.parentAgencyId())
                .orElseThrow(() -> new EntityNotFoundException("Parent Agency", dto.parentAgencyId()));
            agency.setParentAgency(parentAgency);
        }

        if (dto.department() != null) {
            agency.setDepartment(dto.department());
        }

        if (dto.tier() != null) {
            agency.setTier(dto.tier());
        }

        if (dto.isActive() != null) {
            agency.setIsActive(dto.isActive());
        }

        Agency updatedAgency = agencyRepository.save(agency);
        return AgencyResponseDTO.fromEntity(updatedAgency);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Agency agency = agencyRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Agency", id));

        // Soft delete
        agency.setIsActive(false);
        agencyRepository.save(agency);
    }

    @Override
    public Optional<AgencyResponseDTO> findByAbbreviation(String abbreviation) {
        return agencyRepository.findByAbbreviation(abbreviation)
            .map(AgencyResponseDTO::fromEntity);
    }

    @Override
    public List<AgencyResponseDTO> searchByName(String name) {
        return agencyRepository.findByNameContainingIgnoreCase(name)
            .stream()
            .map(AgencyResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<AgencyResponseDTO> findActiveAgencies() {
        return agencyRepository.findByIsActiveTrue()
            .stream()
            .map(AgencyResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<AgencyResponseDTO> findSubAgencies(UUID parentAgencyId) {
        return agencyRepository.findByParentAgencyId(parentAgencyId)
            .stream()
            .map(AgencyResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<AgencyResponseDTO> findByDepartment(String department) {
        return agencyRepository.findByDepartment(department)
            .stream()
            .map(AgencyResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }
}
