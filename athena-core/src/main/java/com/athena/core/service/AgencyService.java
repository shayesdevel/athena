package com.athena.core.service;

import com.athena.core.dto.AgencyCreateDTO;
import com.athena.core.dto.AgencyResponseDTO;
import com.athena.core.dto.AgencyUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for Agency entity operations.
 * Manages federal government agencies and hierarchical relationships.
 */
public interface AgencyService {

    /**
     * Create a new agency.
     *
     * @param dto the agency creation data
     * @return the created agency
     */
    AgencyResponseDTO create(AgencyCreateDTO dto);

    /**
     * Find agency by ID.
     *
     * @param id the agency UUID
     * @return Optional containing the agency if found
     */
    Optional<AgencyResponseDTO> findById(UUID id);

    /**
     * Find all agencies with pagination.
     *
     * @param pageable pagination parameters
     * @return page of agencies
     */
    Page<AgencyResponseDTO> findAll(Pageable pageable);

    /**
     * Update an existing agency.
     *
     * @param id the agency UUID
     * @param dto the update data
     * @return the updated agency
     * @throws com.athena.core.exception.EntityNotFoundException if agency not found
     */
    AgencyResponseDTO update(UUID id, AgencyUpdateDTO dto);

    /**
     * Delete an agency (soft delete by setting isActive to false).
     *
     * @param id the agency UUID
     * @throws com.athena.core.exception.EntityNotFoundException if agency not found
     */
    void delete(UUID id);

    /**
     * Find agency by abbreviation.
     *
     * @param abbreviation the agency abbreviation
     * @return Optional containing the agency if found
     */
    Optional<AgencyResponseDTO> findByAbbreviation(String abbreviation);

    /**
     * Search agencies by name (partial match).
     *
     * @param name the name pattern to search for
     * @return List of matching agencies
     */
    List<AgencyResponseDTO> searchByName(String name);

    /**
     * Find all active agencies.
     *
     * @return List of active agencies
     */
    List<AgencyResponseDTO> findActiveAgencies();

    /**
     * Find sub-agencies by parent agency ID.
     *
     * @param parentAgencyId the parent agency UUID
     * @return List of sub-agencies
     */
    List<AgencyResponseDTO> findSubAgencies(UUID parentAgencyId);

    /**
     * Find agencies by department.
     *
     * @param department the department name
     * @return List of agencies in the department
     */
    List<AgencyResponseDTO> findByDepartment(String department);
}
