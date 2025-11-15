package com.athena.core.service;

import com.athena.core.dto.OrganizationCreateDTO;
import com.athena.core.dto.OrganizationResponseDTO;
import com.athena.core.dto.OrganizationUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for Organization entity operations.
 * Manages contractor organizations and SAM.gov registration data.
 */
public interface OrganizationService {

    /**
     * Create a new organization.
     *
     * @param dto the organization creation data
     * @return the created organization
     * @throws com.athena.core.exception.DuplicateEntityException if UEI already exists
     */
    OrganizationResponseDTO create(OrganizationCreateDTO dto);

    /**
     * Find organization by ID.
     *
     * @param id the organization UUID
     * @return Optional containing the organization if found
     */
    Optional<OrganizationResponseDTO> findById(UUID id);

    /**
     * Find all organizations with pagination.
     *
     * @param pageable pagination parameters
     * @return page of organizations
     */
    Page<OrganizationResponseDTO> findAll(Pageable pageable);

    /**
     * Update an existing organization.
     *
     * @param id the organization UUID
     * @param dto the update data
     * @return the updated organization
     * @throws com.athena.core.exception.EntityNotFoundException if organization not found
     */
    OrganizationResponseDTO update(UUID id, OrganizationUpdateDTO dto);

    /**
     * Delete an organization.
     *
     * @param id the organization UUID
     * @throws com.athena.core.exception.EntityNotFoundException if organization not found
     */
    void delete(UUID id);

    /**
     * Find organization by UEI.
     *
     * @param uei the Unique Entity Identifier
     * @return Optional containing the organization if found
     */
    Optional<OrganizationResponseDTO> findByUei(String uei);

    /**
     * Find organization by CAGE code.
     *
     * @param cageCode the CAGE code
     * @return Optional containing the organization if found
     */
    Optional<OrganizationResponseDTO> findByCageCode(String cageCode);

    /**
     * Search organizations by name (partial match).
     *
     * @param name the name pattern to search for
     * @return List of matching organizations
     */
    List<OrganizationResponseDTO> searchByName(String name);

    /**
     * Find organizations by primary NAICS code.
     *
     * @param primaryNaics the NAICS code
     * @return List of organizations with matching NAICS
     */
    List<OrganizationResponseDTO> findByPrimaryNaics(String primaryNaics);

    /**
     * Find all small business organizations.
     *
     * @return List of small business organizations
     */
    List<OrganizationResponseDTO> findSmallBusinesses();

    /**
     * Check if UEI already exists.
     *
     * @param uei the UEI to check
     * @return true if UEI exists, false otherwise
     */
    boolean existsByUei(String uei);
}
