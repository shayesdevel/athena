package com.athena.core.service;

import com.athena.core.dto.TeamCreateDTO;
import com.athena.core.dto.TeamResponseDTO;
import com.athena.core.dto.TeamUpdateDTO;
import com.athena.core.entity.Team;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.TeamRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of TeamService.
 */
@Service
@Transactional(readOnly = true)
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;

    public TeamServiceImpl(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    @Transactional
    public TeamResponseDTO create(TeamCreateDTO dto) {
        Team team = new Team(
            dto.leadOrganizationId(),
            dto.opportunityId(),
            dto.teamName(),
            dto.status(),
            dto.createdBy()
        );

        Team saved = teamRepository.save(team);
        return TeamResponseDTO.fromEntity(saved);
    }

    @Override
    public Optional<TeamResponseDTO> findById(UUID id) {
        return teamRepository.findById(id)
            .map(TeamResponseDTO::fromEntity);
    }

    @Override
    public Page<TeamResponseDTO> findAll(Pageable pageable) {
        return teamRepository.findAll(pageable)
            .map(TeamResponseDTO::fromEntity);
    }

    @Override
    @Transactional
    public TeamResponseDTO update(UUID id, TeamUpdateDTO dto) {
        Team team = teamRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Team", id));

        updateEntityFromDto(dto, team);

        Team updated = teamRepository.save(team);
        return TeamResponseDTO.fromEntity(updated);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!teamRepository.existsById(id)) {
            throw new EntityNotFoundException("Team", id);
        }
        teamRepository.deleteById(id);
    }

    @Override
    public List<TeamResponseDTO> findByLeadOrganizationId(UUID leadOrganizationId) {
        return teamRepository.findByLeadOrganizationId(leadOrganizationId)
            .stream()
            .map(TeamResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<TeamResponseDTO> findByOpportunityId(UUID opportunityId) {
        return teamRepository.findByOpportunityId(opportunityId)
            .stream()
            .map(TeamResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<TeamResponseDTO> findByStatus(String status) {
        return teamRepository.findByStatus(status)
            .stream()
            .map(TeamResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<TeamResponseDTO> findByCreatedBy(UUID createdBy) {
        return teamRepository.findByCreatedBy(createdBy)
            .stream()
            .map(TeamResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Update entity fields from update DTO (only non-null fields).
     */
    private void updateEntityFromDto(TeamUpdateDTO dto, Team team) {
        if (dto.leadOrganizationId() != null) team.setLeadOrganizationId(dto.leadOrganizationId());
        if (dto.opportunityId() != null) team.setOpportunityId(dto.opportunityId());
        if (dto.teamName() != null) team.setTeamName(dto.teamName());
        if (dto.status() != null) team.setStatus(dto.status());
    }
}
