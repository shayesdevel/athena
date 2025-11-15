package com.athena.core.service;

import com.athena.core.dto.TeamCreateDTO;
import com.athena.core.dto.TeamResponseDTO;
import com.athena.core.dto.TeamUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for Team entity operations.
 * Handles contractor teaming arrangements and collaboration.
 */
public interface TeamService {

    /**
     * Create a new team.
     *
     * @param dto the team creation data
     * @return the created team
     */
    TeamResponseDTO create(TeamCreateDTO dto);

    /**
     * Find team by ID.
     *
     * @param id the team UUID
     * @return Optional containing the team if found
     */
    Optional<TeamResponseDTO> findById(UUID id);

    /**
     * Find all teams with pagination.
     *
     * @param pageable pagination parameters
     * @return page of teams
     */
    Page<TeamResponseDTO> findAll(Pageable pageable);

    /**
     * Update an existing team.
     *
     * @param id the team UUID
     * @param dto the update data
     * @return the updated team
     * @throws com.athena.core.exception.EntityNotFoundException if team not found
     */
    TeamResponseDTO update(UUID id, TeamUpdateDTO dto);

    /**
     * Delete a team.
     *
     * @param id the team UUID
     * @throws com.athena.core.exception.EntityNotFoundException if team not found
     */
    void delete(UUID id);

    /**
     * Find teams by lead organization ID.
     *
     * @param leadOrganizationId the lead organization UUID
     * @return list of teams for the organization
     */
    List<TeamResponseDTO> findByLeadOrganizationId(UUID leadOrganizationId);

    /**
     * Find teams by opportunity ID.
     *
     * @param opportunityId the opportunity UUID
     * @return list of teams for the opportunity
     */
    List<TeamResponseDTO> findByOpportunityId(UUID opportunityId);

    /**
     * Find teams by status.
     *
     * @param status the team status
     * @return list of teams with matching status
     */
    List<TeamResponseDTO> findByStatus(String status);

    /**
     * Find teams created by a specific user.
     *
     * @param createdBy the user UUID
     * @return list of teams created by the user
     */
    List<TeamResponseDTO> findByCreatedBy(UUID createdBy);
}
