package com.athena.core.service;

import com.athena.core.dto.*;
import com.athena.core.entity.Team;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceImplTest {
    @Mock private TeamRepository teamRepository;
    @InjectMocks private TeamServiceImpl teamService;
    private Team testTeam;
    private UUID testTeamId, testOrgId, testOppId, testUserId;

    @BeforeEach
    void setUp() {
        testTeamId = UUID.randomUUID();
        testOrgId = UUID.randomUUID();
        testOppId = UUID.randomUUID();
        testUserId = UUID.randomUUID();
        testTeam = new Team(testOrgId, testOppId, "Alpha Team", "active", testUserId);
        testTeam.setId(testTeamId);
    }

    @Test void create_ShouldCreateTeam() {
        when(teamRepository.save(any(Team.class))).thenReturn(testTeam);
        TeamResponseDTO result = teamService.create(new TeamCreateDTO(testOrgId, testOppId, "Alpha Team", "active", testUserId));
        assertThat(result).isNotNull();
    }

    @Test void findById_ShouldReturnTeam() {
        when(teamRepository.findById(testTeamId)).thenReturn(Optional.of(testTeam));
        Optional<TeamResponseDTO> result = teamService.findById(testTeamId);
        assertThat(result).isPresent();
    }

    @Test void delete_ShouldDeleteTeam() {
        when(teamRepository.existsById(testTeamId)).thenReturn(true);
        teamService.delete(testTeamId);
        verify(teamRepository).deleteById(testTeamId);
    }

    @Test void findByOpportunityId_ShouldReturnTeams() {
        when(teamRepository.findByOpportunityId(testOppId)).thenReturn(Arrays.asList(testTeam));
        List<TeamResponseDTO> result = teamService.findByOpportunityId(testOppId);
        assertThat(result).hasSize(1);
    }
}
