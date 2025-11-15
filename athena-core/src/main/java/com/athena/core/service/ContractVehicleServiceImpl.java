package com.athena.core.service;

import com.athena.core.dto.ContractVehicleCreateDTO;
import com.athena.core.dto.ContractVehicleResponseDTO;
import com.athena.core.dto.ContractVehicleUpdateDTO;
import com.athena.core.entity.ContractVehicle;
import com.athena.core.exception.DuplicateEntityException;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.ContractVehicleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of ContractVehicleService.
 */
@Service
@Transactional(readOnly = true)
public class ContractVehicleServiceImpl implements ContractVehicleService {

    private final ContractVehicleRepository contractVehicleRepository;

    public ContractVehicleServiceImpl(ContractVehicleRepository contractVehicleRepository) {
        this.contractVehicleRepository = contractVehicleRepository;
    }

    @Override
    @Transactional
    public ContractVehicleResponseDTO create(ContractVehicleCreateDTO dto) {
        // Check for duplicates
        if (contractVehicleRepository.existsByCode(dto.code())) {
            throw new DuplicateEntityException("ContractVehicle", "code", dto.code());
        }

        // Create contract vehicle entity
        ContractVehicle contractVehicle = new ContractVehicle(dto.code(), dto.name());
        contractVehicle.setDescription(dto.description());
        contractVehicle.setCategory(dto.category());
        contractVehicle.setManagingAgency(dto.managingAgency());
        contractVehicle.setUrl(dto.url());

        // Save and return
        ContractVehicle savedContractVehicle = contractVehicleRepository.save(contractVehicle);
        return ContractVehicleResponseDTO.fromEntity(savedContractVehicle);
    }

    @Override
    public Optional<ContractVehicleResponseDTO> findById(UUID id) {
        return contractVehicleRepository.findById(id)
            .map(ContractVehicleResponseDTO::fromEntity);
    }

    @Override
    public Page<ContractVehicleResponseDTO> findAll(Pageable pageable) {
        return contractVehicleRepository.findAll(pageable)
            .map(ContractVehicleResponseDTO::fromEntity);
    }

    @Override
    @Transactional
    public ContractVehicleResponseDTO update(UUID id, ContractVehicleUpdateDTO dto) {
        ContractVehicle contractVehicle = contractVehicleRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("ContractVehicle", id));

        // Update fields if provided
        if (dto.name() != null) {
            contractVehicle.setName(dto.name());
        }

        if (dto.description() != null) {
            contractVehicle.setDescription(dto.description());
        }

        if (dto.category() != null) {
            contractVehicle.setCategory(dto.category());
        }

        if (dto.managingAgency() != null) {
            contractVehicle.setManagingAgency(dto.managingAgency());
        }

        if (dto.url() != null) {
            contractVehicle.setUrl(dto.url());
        }

        if (dto.isActive() != null) {
            contractVehicle.setIsActive(dto.isActive());
        }

        ContractVehicle updatedContractVehicle = contractVehicleRepository.save(contractVehicle);
        return ContractVehicleResponseDTO.fromEntity(updatedContractVehicle);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        ContractVehicle contractVehicle = contractVehicleRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("ContractVehicle", id));

        // Soft delete
        contractVehicle.setIsActive(false);
        contractVehicleRepository.save(contractVehicle);
    }

    @Override
    public Optional<ContractVehicleResponseDTO> findByCode(String code) {
        return contractVehicleRepository.findByCode(code)
            .map(ContractVehicleResponseDTO::fromEntity);
    }

    @Override
    public boolean existsByCode(String code) {
        return contractVehicleRepository.existsByCode(code);
    }
}
