package com.athena.core.service;

import com.athena.core.dto.TeamMemberCreateDTO;
import com.athena.core.dto.TeamMemberResponseDTO;
import com.athena.core.dto.TeamMemberUpdateDTO;
import com.athena.core.entity.TeamMember;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.TeamMemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TeamMemberServiceImpl implements TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;

    public TeamMemberServiceImpl(TeamMemberRepository teamMemberRepository) {
        this.teamMemberRepository = teamMemberRepository;
    }

    @Override
    @Transactional
    public TeamMemberResponseDTO create(TeamMemberCreateDTO dto) {
        TeamMember teamMember = new TeamMember(dto.teamId(), dto.organizationId(), dto.role(), dto.addedBy());
        if (dto.capabilities() != null) teamMember.setCapabilities(dto.capabilities());
        if (dto.isPrime() != null) teamMember.setIsPrime(dto.isPrime());
        return TeamMemberResponseDTO.fromEntity(teamMemberRepository.save(teamMember));
    }

    @Override
    public Optional<TeamMemberResponseDTO> findById(UUID id) {
        return teamMemberRepository.findById(id).map(TeamMemberResponseDTO::fromEntity);
    }

    @Override
    public Page<TeamMemberResponseDTO> findAll(Pageable pageable) {
        return teamMemberRepository.findAll(pageable).map(TeamMemberResponseDTO::fromEntity);
    }

    @Override
    @Transactional
    public TeamMemberResponseDTO update(UUID id, TeamMemberUpdateDTO dto) {
        TeamMember teamMember = teamMemberRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("TeamMember", id));
        if (dto.role() != null) teamMember.setRole(dto.role());
        if (dto.capabilities() != null) teamMember.setCapabilities(dto.capabilities());
        if (dto.isPrime() != null) teamMember.setIsPrime(dto.isPrime());
        return TeamMemberResponseDTO.fromEntity(teamMemberRepository.save(teamMember));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!teamMemberRepository.existsById(id)) throw new EntityNotFoundException("TeamMember", id);
        teamMemberRepository.deleteById(id);
    }

    @Override
    public List<TeamMemberResponseDTO> findByTeamId(UUID teamId) {
        return teamMemberRepository.findByTeamId(teamId).stream()
            .map(TeamMemberResponseDTO::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<TeamMemberResponseDTO> findByOrganizationId(UUID organizationId) {
        return teamMemberRepository.findByOrganizationId(organizationId).stream()
            .map(TeamMemberResponseDTO::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<TeamMemberResponseDTO> findPrimeContractorsByTeamId(UUID teamId) {
        return teamMemberRepository.findByTeamIdAndIsPrime(teamId, true).stream()
            .map(TeamMemberResponseDTO::fromEntity).collect(Collectors.toList());
    }
}
