package com.athena.core.repository;

import com.athena.core.AbstractIntegrationTest;
import com.athena.core.entity.Award;
import com.athena.core.entity.Opportunity;
import com.athena.core.entity.Organization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for AwardRepository using Testcontainers.
 */
@DataJpaTest



class AwardRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private AwardRepository awardRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private Opportunity testOpportunity;
    private Organization testOrganization;

    @BeforeEach
    void setUp() {
        // Create test opportunity
        testOpportunity = new Opportunity("TEST-OPP-001", "Test Opportunity", "Solicitation");
        testOpportunity = opportunityRepository.save(testOpportunity);

        // Create test organization
        testOrganization = new Organization("Test Contractor LLC");
        testOrganization.setUei("ABC123456789");
        testOrganization = organizationRepository.save(testOrganization);
    }

    @Test
    void shouldSaveAndRetrieveAward() {
        // Given
        Award award = new Award("CONTRACT-2024-001");
        award.setOpportunity(testOpportunity);
        award.setOrganization(testOrganization);
        award.setTitle("IT Services Contract");
        award.setAwardeeName("Test Contractor LLC");
        award.setAwardeeUei("ABC123456789");
        award.setAwardDate(LocalDate.of(2024, 1, 15));
        award.setAwardAmount(new BigDecimal("1500000.00"));
        award.setCurrency("USD");
        award.setStartDate(LocalDate.of(2024, 2, 1));
        award.setEndDate(LocalDate.of(2025, 1, 31));
        award.setAwardingOffice("Department of Defense - DLA");
        award.setAwardType("Definite");
        award.setNaicsCode("541512");
        award.setSetAside("Small Business");
        award.setDescription("IT services and support");

        // When
        Award saved = awardRepository.save(award);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getContractNumber()).isEqualTo("CONTRACT-2024-001");
        assertThat(saved.getTitle()).isEqualTo("IT Services Contract");
        assertThat(saved.getOrganization().getId()).isEqualTo(testOrganization.getId());
        assertThat(saved.getAwardeeName()).isEqualTo("Test Contractor LLC");
        assertThat(saved.getAwardeeUei()).isEqualTo("ABC123456789");
        assertThat(saved.getAwardDate()).isEqualTo(LocalDate.of(2024, 1, 15));
        assertThat(saved.getAwardAmount()).isEqualByComparingTo(new BigDecimal("1500000.00"));
        assertThat(saved.getCurrency()).isEqualTo("USD");
        assertThat(saved.getStartDate()).isEqualTo(LocalDate.of(2024, 2, 1));
        assertThat(saved.getEndDate()).isEqualTo(LocalDate.of(2025, 1, 31));
        assertThat(saved.getAwardType()).isEqualTo("Definite");
        assertThat(saved.getNaicsCode()).isEqualTo("541512");
        assertThat(saved.getSetAside()).isEqualTo("Small Business");
        assertThat(saved.getIsActive()).isTrue();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldFindAwardByContractNumber() {
        // Given
        Award award = new Award("CONTRACT-2024-002");
        award.setAwardeeName("Acme Corp");
        awardRepository.save(award);

        // When
        Optional<Award> found = awardRepository.findByContractNumber("CONTRACT-2024-002");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getContractNumber()).isEqualTo("CONTRACT-2024-002");
        assertThat(found.get().getAwardeeName()).isEqualTo("Acme Corp");
    }

    @Test
    void shouldFindAwardsByOpportunity() {
        // Given
        Award award1 = new Award("CONTRACT-2024-001");
        award1.setOpportunity(testOpportunity);
        Award award2 = new Award("CONTRACT-2024-002");
        award2.setOpportunity(testOpportunity);

        awardRepository.save(award1);
        awardRepository.save(award2);

        // When
        List<Award> awards = awardRepository.findByOpportunity(testOpportunity);

        // Then
        assertThat(awards).hasSize(2);
        assertThat(awards).extracting(Award::getContractNumber)
            .containsExactlyInAnyOrder("CONTRACT-2024-001", "CONTRACT-2024-002");
    }

    @Test
    void shouldFindAwardsByOrganization() {
        // Given
        Award award1 = new Award("CONTRACT-2024-001");
        award1.setOrganization(testOrganization);
        Award award2 = new Award("CONTRACT-2024-002");
        award2.setOrganization(testOrganization);

        awardRepository.save(award1);
        awardRepository.save(award2);

        // When
        List<Award> awards = awardRepository.findByOrganization(testOrganization);

        // Then
        assertThat(awards).hasSize(2);
    }

    @Test
    void shouldFindAwardsByAwardeeUei() {
        // Given
        Award award1 = new Award("CONTRACT-2024-001");
        award1.setAwardeeUei("ABC123456789");
        Award award2 = new Award("CONTRACT-2024-002");
        award2.setAwardeeUei("ABC123456789");
        Award award3 = new Award("CONTRACT-2024-003");
        award3.setAwardeeUei("XYZ987654321");

        awardRepository.save(award1);
        awardRepository.save(award2);
        awardRepository.save(award3);

        // When
        List<Award> awards = awardRepository.findByAwardeeUei("ABC123456789");

        // Then
        assertThat(awards).hasSize(2);
        assertThat(awards).extracting(Award::getContractNumber)
            .containsExactlyInAnyOrder("CONTRACT-2024-001", "CONTRACT-2024-002");
    }

    @Test
    void shouldFindActiveAwards() {
        // Given
        Award active1 = new Award("CONTRACT-2024-001");
        Award active2 = new Award("CONTRACT-2024-002");
        Award inactive = new Award("CONTRACT-2020-999");
        inactive.setIsActive(false);

        awardRepository.save(active1);
        awardRepository.save(active2);
        awardRepository.save(inactive);

        // When
        List<Award> activeAwards = awardRepository.findByIsActiveTrue();

        // Then
        assertThat(activeAwards).hasSize(2);
        assertThat(activeAwards).extracting(Award::getContractNumber)
            .containsExactlyInAnyOrder("CONTRACT-2024-001", "CONTRACT-2024-002");
    }

    @Test
    void shouldFindAwardsByDateRange() {
        // Given
        Award award2023 = new Award("CONTRACT-2023-001");
        award2023.setAwardDate(LocalDate.of(2023, 6, 15));
        Award award2024a = new Award("CONTRACT-2024-001");
        award2024a.setAwardDate(LocalDate.of(2024, 3, 1));
        Award award2024b = new Award("CONTRACT-2024-002");
        award2024b.setAwardDate(LocalDate.of(2024, 8, 15));
        Award award2025 = new Award("CONTRACT-2025-001");
        award2025.setAwardDate(LocalDate.of(2025, 1, 10));

        awardRepository.save(award2023);
        awardRepository.save(award2024a);
        awardRepository.save(award2024b);
        awardRepository.save(award2025);

        // When
        List<Award> awards2024 = awardRepository.findByAwardDateBetween(
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 12, 31)
        );

        // Then
        assertThat(awards2024).hasSize(2);
        assertThat(awards2024).extracting(Award::getContractNumber)
            .containsExactlyInAnyOrder("CONTRACT-2024-001", "CONTRACT-2024-002");
    }

    @Test
    void shouldFindAwardsByNaicsCode() {
        // Given
        Award award1 = new Award("CONTRACT-2024-001");
        award1.setNaicsCode("541512");
        Award award2 = new Award("CONTRACT-2024-002");
        award2.setNaicsCode("541512");
        Award award3 = new Award("CONTRACT-2024-003");
        award3.setNaicsCode("541511");

        awardRepository.save(award1);
        awardRepository.save(award2);
        awardRepository.save(award3);

        // When
        List<Award> awards = awardRepository.findByNaicsCode("541512");

        // Then
        assertThat(awards).hasSize(2);
        assertThat(awards).extracting(Award::getContractNumber)
            .containsExactlyInAnyOrder("CONTRACT-2024-001", "CONTRACT-2024-002");
    }

    @Test
    void shouldFindAwardsBySetAside() {
        // Given
        Award smallBiz1 = new Award("CONTRACT-2024-001");
        smallBiz1.setSetAside("Small Business");
        Award smallBiz2 = new Award("CONTRACT-2024-002");
        smallBiz2.setSetAside("Small Business");
        Award eightA = new Award("CONTRACT-2024-003");
        eightA.setSetAside("8(a)");

        awardRepository.save(smallBiz1);
        awardRepository.save(smallBiz2);
        awardRepository.save(eightA);

        // When
        List<Award> smallBizAwards = awardRepository.findBySetAside("Small Business");

        // Then
        assertThat(smallBizAwards).hasSize(2);
        assertThat(smallBizAwards).extracting(Award::getContractNumber)
            .containsExactlyInAnyOrder("CONTRACT-2024-001", "CONTRACT-2024-002");
    }

    @Test
    void shouldCountAwardsByOrganization() {
        // Given
        Award award1 = new Award("CONTRACT-2024-001");
        award1.setOrganization(testOrganization);
        Award award2 = new Award("CONTRACT-2024-002");
        award2.setOrganization(testOrganization);
        Award award3 = new Award("CONTRACT-2024-003");
        award3.setOrganization(testOrganization);

        awardRepository.save(award1);
        awardRepository.save(award2);
        awardRepository.save(award3);

        // When
        long count = awardRepository.countByOrganization(testOrganization);

        // Then
        assertThat(count).isEqualTo(3);
    }
}
