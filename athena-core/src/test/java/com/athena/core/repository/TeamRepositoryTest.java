package com.athena.core.repository;

import com.athena.core.AbstractIntegrationTest;
import com.athena.core.entity.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TeamRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private TeamRepository teamRepository;

    @Test
    void shouldSaveAndRetrieveTeam() {
        // Arrange
        UUID leadOrgId = UUID.randomUUID();
        UUID opportunityId = UUID.randomUUID();
        UUID createdBy = UUID.randomUUID();

        Team team = new Team(leadOrgId, opportunityId, "Alpha Team", "forming", createdBy);

        // Act
        Team saved = teamRepository.save(team);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getLeadOrganizationId()).isEqualTo(leadOrgId);
        assertThat(saved.getOpportunityId()).isEqualTo(opportunityId);
        assertThat(saved.getTeamName()).isEqualTo("Alpha Team");
        assertThat(saved.getStatus()).isEqualTo("forming");
        assertThat(saved.getCreatedBy()).isEqualTo(createdBy);
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldFindByLeadOrganizationId() {
        // Arrange
        UUID leadOrgId = UUID.randomUUID();
        UUID createdBy = UUID.randomUUID();

        teamRepository.save(new Team(leadOrgId, UUID.randomUUID(), "Team 1", "active", createdBy));
        teamRepository.save(new Team(leadOrgId, UUID.randomUUID(), "Team 2", "forming", createdBy));

        // Act
        List<Team> teams = teamRepository.findByLeadOrganizationId(leadOrgId);

        // Assert
        assertThat(teams).hasSize(2);
        assertThat(teams).extracting(Team::getLeadOrganizationId)
                .containsOnly(leadOrgId);
    }

    @Test
    void shouldFindByOpportunityId() {
        // Arrange
        UUID opportunityId = UUID.randomUUID();
        UUID createdBy = UUID.randomUUID();

        teamRepository.save(new Team(UUID.randomUUID(), opportunityId, "Team A", "active", createdBy));

        // Act
        List<Team> teams = teamRepository.findByOpportunityId(opportunityId);

        // Assert
        assertThat(teams).hasSize(1);
        assertThat(teams.get(0).getOpportunityId()).isEqualTo(opportunityId);
    }

    @Test
    void shouldFindByStatus() {
        // Arrange
        UUID createdBy = UUID.randomUUID();

        teamRepository.save(new Team(UUID.randomUUID(), UUID.randomUUID(), "Team X", "active", createdBy));
        teamRepository.save(new Team(UUID.randomUUID(), UUID.randomUUID(), "Team Y", "active", createdBy));

        // Act
        List<Team> activeTeams = teamRepository.findByStatus("active");

        // Assert
        assertThat(activeTeams).hasSizeGreaterThanOrEqualTo(2);
        assertThat(activeTeams).extracting(Team::getStatus)
                .containsOnly("active");
    }

    @Test
    void shouldFindByCreatedBy() {
        // Arrange
        UUID createdBy = UUID.randomUUID();

        teamRepository.save(new Team(UUID.randomUUID(), UUID.randomUUID(), "Team 1", "forming", createdBy));
        teamRepository.save(new Team(UUID.randomUUID(), UUID.randomUUID(), "Team 2", "active", createdBy));

        // Act
        List<Team> teams = teamRepository.findByCreatedBy(createdBy);

        // Assert
        assertThat(teams).hasSize(2);
        assertThat(teams).extracting(Team::getCreatedBy)
                .containsOnly(createdBy);
    }

    @Test
    void shouldFindByLeadOrganizationAndOpportunity() {
        // Arrange
        UUID leadOrgId = UUID.randomUUID();
        UUID opportunityId = UUID.randomUUID();
        UUID createdBy = UUID.randomUUID();

        teamRepository.save(new Team(leadOrgId, opportunityId, "Specific Team", "forming", createdBy));

        // Act
        var found = teamRepository.findByLeadOrganizationIdAndOpportunityId(leadOrgId, opportunityId);

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getTeamName()).isEqualTo("Specific Team");
    }

    @Test
    void shouldCheckIfTeamExistsForOpportunity() {
        // Arrange
        UUID opportunityId = UUID.randomUUID();
        UUID createdBy = UUID.randomUUID();

        teamRepository.save(new Team(UUID.randomUUID(), opportunityId, "Team", "active", createdBy));

        // Act
        boolean exists = teamRepository.existsByOpportunityId(opportunityId);
        boolean notExists = teamRepository.existsByOpportunityId(UUID.randomUUID());

        // Assert
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}
