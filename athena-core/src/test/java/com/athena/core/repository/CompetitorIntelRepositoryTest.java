package com.athena.core.repository;

import com.athena.core.AbstractIntegrationTest;
import com.athena.core.entity.CompetitorIntel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CompetitorIntelRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private CompetitorIntelRepository competitorIntelRepository;

    @Test
    void shouldSaveAndRetrieveCompetitorIntel() {
        // Arrange
        UUID organizationId = UUID.randomUUID();
        UUID opportunityId = UUID.randomUUID();

        CompetitorIntel intel = new CompetitorIntel(organizationId, opportunityId, "internal");
        intel.setLikelihood("high");
        intel.setStrengths("Strong past performance, incumbent");
        intel.setWeaknesses("Higher pricing");

        // Act
        CompetitorIntel saved = competitorIntelRepository.save(intel);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getOrganizationId()).isEqualTo(organizationId);
        assertThat(saved.getOpportunityId()).isEqualTo(opportunityId);
        assertThat(saved.getLikelihood()).isEqualTo("high");
        assertThat(saved.getStrengths()).isEqualTo("Strong past performance, incumbent");
        assertThat(saved.getWeaknesses()).isEqualTo("Higher pricing");
        assertThat(saved.getSource()).isEqualTo("internal");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldFindByOpportunityId() {
        // Arrange
        UUID opportunityId = UUID.randomUUID();

        competitorIntelRepository.save(new CompetitorIntel(UUID.randomUUID(), opportunityId, "public"));
        competitorIntelRepository.save(new CompetitorIntel(UUID.randomUUID(), opportunityId, "industry"));

        // Act
        List<CompetitorIntel> intel = competitorIntelRepository.findByOpportunityId(opportunityId);

        // Assert
        assertThat(intel).hasSize(2);
        assertThat(intel).extracting(CompetitorIntel::getOpportunityId)
                .containsOnly(opportunityId);
    }

    @Test
    void shouldFindByOrganizationId() {
        // Arrange
        UUID organizationId = UUID.randomUUID();

        competitorIntelRepository.save(new CompetitorIntel(organizationId, UUID.randomUUID(), "automated"));
        competitorIntelRepository.save(new CompetitorIntel(organizationId, UUID.randomUUID(), "public"));

        // Act
        List<CompetitorIntel> intel = competitorIntelRepository.findByOrganizationId(organizationId);

        // Assert
        assertThat(intel).hasSize(2);
        assertThat(intel).extracting(CompetitorIntel::getOrganizationId)
                .containsOnly(organizationId);
    }

    @Test
    void shouldFindByLikelihood() {
        // Arrange
        CompetitorIntel intel1 = new CompetitorIntel(UUID.randomUUID(), UUID.randomUUID(), "internal");
        intel1.setLikelihood("very_high");
        competitorIntelRepository.save(intel1);

        CompetitorIntel intel2 = new CompetitorIntel(UUID.randomUUID(), UUID.randomUUID(), "public");
        intel2.setLikelihood("very_high");
        competitorIntelRepository.save(intel2);

        // Act
        List<CompetitorIntel> highLikelihood = competitorIntelRepository.findByLikelihood("very_high");

        // Assert
        assertThat(highLikelihood).hasSizeGreaterThanOrEqualTo(2);
        assertThat(highLikelihood).extracting(CompetitorIntel::getLikelihood)
                .containsOnly("very_high");
    }

    @Test
    void shouldFindByOrganizationAndOpportunity() {
        // Arrange
        UUID organizationId = UUID.randomUUID();
        UUID opportunityId = UUID.randomUUID();

        CompetitorIntel intel = new CompetitorIntel(organizationId, opportunityId, "industry");
        intel.setLikelihood("medium");
        competitorIntelRepository.save(intel);

        // Act
        var found = competitorIntelRepository.findByOrganizationIdAndOpportunityId(organizationId, opportunityId);

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getLikelihood()).isEqualTo("medium");
    }

    @Test
    void shouldCheckIfIntelExistsForOpportunity() {
        // Arrange
        UUID opportunityId = UUID.randomUUID();

        competitorIntelRepository.save(new CompetitorIntel(UUID.randomUUID(), opportunityId, "automated"));

        // Act
        boolean exists = competitorIntelRepository.existsByOpportunityId(opportunityId);
        boolean notExists = competitorIntelRepository.existsByOpportunityId(UUID.randomUUID());

        // Assert
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}
