package com.athena.core.repository;

import com.athena.core.AbstractIntegrationTest;
import com.athena.core.entity.TeamMember;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TeamMemberRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Test
    void shouldSaveAndRetrieveTeamMember() {
        // Arrange
        UUID teamId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID addedBy = UUID.randomUUID();

        TeamMember member = new TeamMember(teamId, organizationId, "subcontractor", addedBy);
        member.setCapabilities("Cloud infrastructure, DevSecOps");
        member.setIsPrime(false);

        // Act
        TeamMember saved = teamMemberRepository.save(member);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTeamId()).isEqualTo(teamId);
        assertThat(saved.getOrganizationId()).isEqualTo(organizationId);
        assertThat(saved.getRole()).isEqualTo("subcontractor");
        assertThat(saved.getCapabilities()).isEqualTo("Cloud infrastructure, DevSecOps");
        assertThat(saved.getIsPrime()).isFalse();
        assertThat(saved.getAddedBy()).isEqualTo(addedBy);
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldFindByTeamId() {
        // Arrange
        UUID teamId = UUID.randomUUID();
        UUID addedBy = UUID.randomUUID();

        teamMemberRepository.save(new TeamMember(teamId, UUID.randomUUID(), "prime", addedBy));
        teamMemberRepository.save(new TeamMember(teamId, UUID.randomUUID(), "subcontractor", addedBy));
        teamMemberRepository.save(new TeamMember(teamId, UUID.randomUUID(), "partner", addedBy));

        // Act
        List<TeamMember> members = teamMemberRepository.findByTeamId(teamId);

        // Assert
        assertThat(members).hasSize(3);
        assertThat(members).extracting(TeamMember::getTeamId)
                .containsOnly(teamId);
    }

    @Test
    void shouldFindByOrganizationId() {
        // Arrange
        UUID organizationId = UUID.randomUUID();
        UUID addedBy = UUID.randomUUID();

        teamMemberRepository.save(new TeamMember(UUID.randomUUID(), organizationId, "prime", addedBy));
        teamMemberRepository.save(new TeamMember(UUID.randomUUID(), organizationId, "subcontractor", addedBy));

        // Act
        List<TeamMember> memberships = teamMemberRepository.findByOrganizationId(organizationId);

        // Assert
        assertThat(memberships).hasSize(2);
        assertThat(memberships).extracting(TeamMember::getOrganizationId)
                .containsOnly(organizationId);
    }

    @Test
    void shouldFindPrimeContractors() {
        // Arrange
        UUID teamId = UUID.randomUUID();
        UUID addedBy = UUID.randomUUID();

        TeamMember prime = new TeamMember(teamId, UUID.randomUUID(), "prime", addedBy);
        prime.setIsPrime(true);
        teamMemberRepository.save(prime);

        TeamMember sub = new TeamMember(teamId, UUID.randomUUID(), "subcontractor", addedBy);
        sub.setIsPrime(false);
        teamMemberRepository.save(sub);

        // Act
        List<TeamMember> primes = teamMemberRepository.findByTeamIdAndIsPrime(teamId, true);

        // Assert
        assertThat(primes).hasSize(1);
        assertThat(primes.get(0).getIsPrime()).isTrue();
        assertThat(primes.get(0).getRole()).isEqualTo("prime");
    }

    @Test
    void shouldFindByTeamIdAndOrganizationId() {
        // Arrange
        UUID teamId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID addedBy = UUID.randomUUID();

        teamMemberRepository.save(new TeamMember(teamId, organizationId, "partner", addedBy));

        // Act
        var found = teamMemberRepository.findByTeamIdAndOrganizationId(teamId, organizationId);

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getRole()).isEqualTo("partner");
    }

    @Test
    void shouldCheckIfMemberExists() {
        // Arrange
        UUID teamId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID addedBy = UUID.randomUUID();

        teamMemberRepository.save(new TeamMember(teamId, organizationId, "subcontractor", addedBy));

        // Act
        boolean exists = teamMemberRepository.existsByTeamIdAndOrganizationId(teamId, organizationId);
        boolean notExists = teamMemberRepository.existsByTeamIdAndOrganizationId(teamId, UUID.randomUUID());

        // Assert
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}
