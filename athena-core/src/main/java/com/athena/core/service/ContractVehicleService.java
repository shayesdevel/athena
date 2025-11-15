package com.athena.core.service;

import com.athena.core.dto.ContractVehicleCreateDTO;
import com.athena.core.dto.ContractVehicleResponseDTO;
import com.athena.core.dto.ContractVehicleUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for ContractVehicle entity operations.
 * Handles contract acquisition vehicles (e.g., GSA Schedule, IDIQ, BPA).
 */
public interface ContractVehicleService {

    /**
     * Create a new contract vehicle.
     *
     * @param dto the contract vehicle creation data
     * @return the created contract vehicle
     * @throws com.athena.core.exception.DuplicateEntityException if code already exists
     */
    ContractVehicleResponseDTO create(ContractVehicleCreateDTO dto);

    /**
     * Find contract vehicle by ID.
     *
     * @param id the contract vehicle UUID
     * @return Optional containing the contract vehicle if found
     */
    Optional<ContractVehicleResponseDTO> findById(UUID id);

    /**
     * Find all contract vehicles with pagination.
     *
     * @param pageable pagination parameters
     * @return page of contract vehicles
     */
    Page<ContractVehicleResponseDTO> findAll(Pageable pageable);

    /**
     * Update an existing contract vehicle.
     *
     * @param id the contract vehicle UUID
     * @param dto the update data
     * @return the updated contract vehicle
     * @throws com.athena.core.exception.EntityNotFoundException if contract vehicle not found
     */
    ContractVehicleResponseDTO update(UUID id, ContractVehicleUpdateDTO dto);

    /**
     * Delete a contract vehicle (soft delete by setting isActive to false).
     *
     * @param id the contract vehicle UUID
     * @throws com.athena.core.exception.EntityNotFoundException if contract vehicle not found
     */
    void delete(UUID id);

    /**
     * Find contract vehicle by code.
     *
     * @param code the code to search for
     * @return Optional containing the contract vehicle if found
     */
    Optional<ContractVehicleResponseDTO> findByCode(String code);

    /**
     * Check if code already exists.
     *
     * @param code the code to check
     * @return true if code exists, false otherwise
     */
    boolean existsByCode(String code);
}
