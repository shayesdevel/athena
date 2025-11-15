package com.athena.core.service;

import com.athena.core.dto.TeamMemberCreateDTO;
import com.athena.core.dto.TeamMemberResponseDTO;
import com.athena.core.dto.TeamMemberUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamMemberService {
    TeamMemberResponseDTO create(TeamMemberCreateDTO dto);
    Optional<TeamMemberResponseDTO> findById(UUID id);
    Page<TeamMemberResponseDTO> findAll(Pageable pageable);
    TeamMemberResponseDTO update(UUID id, TeamMemberUpdateDTO dto);
    void delete(UUID id);
    List<TeamMemberResponseDTO> findByTeamId(UUID teamId);
    List<TeamMemberResponseDTO> findByOrganizationId(UUID organizationId);
    List<TeamMemberResponseDTO> findPrimeContractorsByTeamId(UUID teamId);
}
