package com.athena.core.service;

import com.athena.core.dto.*;
import com.athena.core.entity.TeamMember;
import com.athena.core.repository.TeamMemberRepository;
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
class TeamMemberServiceImplTest {
    @Mock private TeamMemberRepository teamMemberRepository;
    @InjectMocks private TeamMemberServiceImpl teamMemberService;
    private TeamMember testTeamMember;
    private UUID testId, testTeamId, testOrgId, testUserId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testTeamId = UUID.randomUUID();
        testOrgId = UUID.randomUUID();
        testUserId = UUID.randomUUID();
        testTeamMember = new TeamMember(testTeamId, testOrgId, "Subcontractor", testUserId);
        testTeamMember.setId(testId);
    }

    @Test void create_ShouldCreateTeamMember() {
        when(teamMemberRepository.save(any(TeamMember.class))).thenReturn(testTeamMember);
        TeamMemberResponseDTO result = teamMemberService.create(new TeamMemberCreateDTO(testTeamId, testOrgId, "Subcontractor", null, false, testUserId));
        assertThat(result).isNotNull();
    }

    @Test void findById_ShouldReturnTeamMember() {
        when(teamMemberRepository.findById(testId)).thenReturn(Optional.of(testTeamMember));
        Optional<TeamMemberResponseDTO> result = teamMemberService.findById(testId);
        assertThat(result).isPresent();
    }

    @Test void findByTeamId_ShouldReturnMembers() {
        when(teamMemberRepository.findByTeamId(testTeamId)).thenReturn(Arrays.asList(testTeamMember));
        List<TeamMemberResponseDTO> result = teamMemberService.findByTeamId(testTeamId);
        assertThat(result).hasSize(1);
    }

    @Test void delete_ShouldDeleteTeamMember() {
        when(teamMemberRepository.existsById(testId)).thenReturn(true);
        teamMemberService.delete(testId);
        verify(teamMemberRepository).deleteById(testId);
    }
}
