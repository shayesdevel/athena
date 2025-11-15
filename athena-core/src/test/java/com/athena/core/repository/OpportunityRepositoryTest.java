package com.athena.core.repository;

import com.athena.core.TestContainersConfiguration;
import com.athena.core.entity.Agency;
import com.athena.core.entity.Opportunity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for OpportunityRepository using Testcontainers.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainersConfiguration.class)
@org.springframework.test.context.ActiveProfiles("test")
class OpportunityRepositoryTest {

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldSaveAndRetrieveOpportunity() {
        // Given
        Opportunity opp = new Opportunity("NOTICE-123", "IT Services Contract", "Solicitation");
        opp.setSolicitationNumber("SOL-2025-001");
        opp.setNaicsCode("541512");
        opp.setPostedDate(LocalDate.of(2025, 11, 15));
        opp.setResponseDeadline(Instant.parse("2025-12-15T23:59:59Z"));

        // When
        Opportunity savedOpp = opportunityRepository.save(opp);

        // Then
        assertThat(savedOpp.getId()).isNotNull();
        assertThat(savedOpp.getNoticeId()).isEqualTo("NOTICE-123");
        assertThat(savedOpp.getTitle()).isEqualTo("IT Services Contract");
        assertThat(savedOpp.getNoticeType()).isEqualTo("Solicitation");
        assertThat(savedOpp.getNaicsCode()).isEqualTo("541512");
        assertThat(savedOpp.getIsActive()).isTrue();
        assertThat(savedOpp.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldFindOpportunityByNoticeId() {
        // Given
        Opportunity opp = new Opportunity("UNIQUE-NOTICE-456", "Test Opportunity", "RFI");
        opportunityRepository.save(opp);

        // When
        Optional<Opportunity> found = opportunityRepository.findByNoticeId("UNIQUE-NOTICE-456");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getNoticeId()).isEqualTo("UNIQUE-NOTICE-456");
    }

    @Test
    void shouldFindActiveOpportunities() {
        // Given
        Opportunity active = new Opportunity("ACTIVE-001", "Active Contract", "Solicitation");
        active.setIsActive(true);
        opportunityRepository.save(active);

        Opportunity inactive = new Opportunity("INACTIVE-001", "Inactive Contract", "Solicitation");
        inactive.setIsActive(false);
        opportunityRepository.save(inactive);

        // When
        List<Opportunity> activeOpps = opportunityRepository.findByIsActiveTrue();

        // Then
        assertThat(activeOpps).hasSize(1);
        assertThat(activeOpps.get(0).getNoticeId()).isEqualTo("ACTIVE-001");
    }

    @Test
    void shouldFindOpportunitiesByNaicsCode() {
        // Given
        Opportunity opp1 = new Opportunity("OPP-IT-001", "IT Contract 1", "Solicitation");
        opp1.setNaicsCode("541512");
        opportunityRepository.save(opp1);

        Opportunity opp2 = new Opportunity("OPP-IT-002", "IT Contract 2", "Solicitation");
        opp2.setNaicsCode("541512");
        opportunityRepository.save(opp2);

        // When
        List<Opportunity> itOpps = opportunityRepository.findByNaicsCode("541512");

        // Then
        assertThat(itOpps).hasSize(2);
    }

    @Test
    void shouldFindOpportunitiesPostedAfterDate() {
        // Given
        Opportunity oldOpp = new Opportunity("OLD-001", "Old Opportunity", "Solicitation");
        oldOpp.setPostedDate(LocalDate.of(2025, 10, 1));
        opportunityRepository.save(oldOpp);

        Opportunity newOpp = new Opportunity("NEW-001", "New Opportunity", "Solicitation");
        newOpp.setPostedDate(LocalDate.of(2025, 11, 15));
        opportunityRepository.save(newOpp);

        // When
        List<Opportunity> recentOpps = opportunityRepository.findByPostedDateAfter(LocalDate.of(2025, 11, 1));

        // Then
        assertThat(recentOpps).hasSize(1);
        assertThat(recentOpps.get(0).getNoticeId()).isEqualTo("NEW-001");
    }

    @Test
    void shouldFindOpportunitiesWithUpcomingDeadlines() {
        // Given
        Instant now = Instant.parse("2025-11-15T00:00:00Z");
        Instant futureDeadline = Instant.parse("2025-11-30T23:59:59Z");

        Opportunity urgentOpp = new Opportunity("URGENT-001", "Urgent Contract", "Solicitation");
        urgentOpp.setResponseDeadline(Instant.parse("2025-11-20T23:59:59Z"));
        urgentOpp.setIsActive(true);
        opportunityRepository.save(urgentOpp);

        Opportunity distantOpp = new Opportunity("DISTANT-001", "Distant Contract", "Solicitation");
        distantOpp.setResponseDeadline(Instant.parse("2025-12-15T23:59:59Z"));
        distantOpp.setIsActive(true);
        opportunityRepository.save(distantOpp);

        // When
        List<Opportunity> upcomingOpps = opportunityRepository
                .findActiveOpportunitiesWithUpcomingDeadlines(now, futureDeadline);

        // Then
        assertThat(upcomingOpps).hasSize(1);
        assertThat(upcomingOpps.get(0).getNoticeId()).isEqualTo("URGENT-001");
    }

    @Test
    void shouldFindOpportunitiesByTitleContaining() {
        // Given
        opportunityRepository.save(new Opportunity("OPP-CYBER-001", "Cybersecurity Services", "Solicitation"));
        opportunityRepository.save(new Opportunity("OPP-CYBER-002", "Network Security Assessment", "RFI"));
        opportunityRepository.save(new Opportunity("OPP-IT-001", "IT Support Services", "Solicitation"));

        // When
        List<Opportunity> securityOpps = opportunityRepository.findByTitleContainingIgnoreCase("security");

        // Then
        assertThat(securityOpps).hasSize(2);
    }
}
