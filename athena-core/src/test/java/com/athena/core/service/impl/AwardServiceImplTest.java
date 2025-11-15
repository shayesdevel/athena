package com.athena.core.service.impl;

import com.athena.core.dto.AwardCreateDTO;
import com.athena.core.dto.AwardResponseDTO;
import com.athena.core.dto.AwardUpdateDTO;
import com.athena.core.entity.Agency;
import com.athena.core.entity.Award;
import com.athena.core.entity.Opportunity;
import com.athena.core.entity.Organization;
import com.athena.core.exception.DuplicateEntityException;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.AgencyRepository;
import com.athena.core.repository.AwardRepository;
import com.athena.core.repository.OpportunityRepository;
import com.athena.core.repository.OrganizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AwardServiceImplTest {

    @Mock
    private AwardRepository awardRepository;

    @Mock
    private OpportunityRepository opportunityRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private AgencyRepository agencyRepository;

    @InjectMocks
    private AwardServiceImpl awardService;

    private Award testAward;
    private Organization testOrganization;
    private Opportunity testOpportunity;
    private Agency testAgency;
    private UUID testAwardId;
    private UUID testOrganizationId;
    private UUID testOpportunityId;
    private UUID testAgencyId;

    @BeforeEach
    void setUp() {
        testAwardId = UUID.randomUUID();
        testOrganizationId = UUID.randomUUID();
        testOpportunityId = UUID.randomUUID();
        testAgencyId = UUID.randomUUID();

        testOrganization = new Organization();
        testOrganization.setId(testOrganizationId);
        testOrganization.setName("Acme Corporation");

        testOpportunity = new Opportunity("NOTICE-001", "Test Opportunity", "Presolicitation");
        testOpportunity.setId(testOpportunityId);

        testAgency = new Agency();
        testAgency.setId(testAgencyId);
        testAgency.setName("Department of Defense");

        testAward = new Award("CONTRACT-2024-001");
        testAward.setId(testAwardId);
        testAward.setTitle("IT Services Contract");
        testAward.setOrganization(testOrganization);
        testAward.setOpportunity(testOpportunity);
        testAward.setAgency(testAgency);
        testAward.setAwardeeName("Acme Corporation");
        testAward.setAwardeeUei("ABC123456789");
        testAward.setAwardDate(LocalDate.of(2024, 1, 15));
        testAward.setAwardAmount(new BigDecimal("1000000.00"));
        testAward.setCurrency("USD");
        testAward.setNaicsCode("541512");
        testAward.setIsActive(true);
        testAward.setCreatedAt(Instant.now());
        testAward.setUpdatedAt(Instant.now());
    }

    @Test
    void create_ShouldCreateAward_WhenValidData() {
        // Given
        AwardCreateDTO dto = new AwardCreateDTO(
            testOpportunityId,
            "CONTRACT-2024-002",
            "New Contract",
            testOrganizationId,
            "Test Company",
            "UEI123456789",
            "123456789",
            LocalDate.of(2024, 2, 1),
            new BigDecimal("500000.00"),
            "USD",
            LocalDate.of(2024, 3, 1),
            LocalDate.of(2025, 2, 28),
            testAgencyId,
            "Test Office",
            "Definite",
            "541519",
            "Small Business",
            "Test description"
        );

        when(awardRepository.findByContractNumber(dto.contractNumber())).thenReturn(Optional.empty());
        when(opportunityRepository.findById(testOpportunityId)).thenReturn(Optional.of(testOpportunity));
        when(organizationRepository.findById(testOrganizationId)).thenReturn(Optional.of(testOrganization));
        when(agencyRepository.findById(testAgencyId)).thenReturn(Optional.of(testAgency));
        when(awardRepository.save(any(Award.class))).thenReturn(testAward);

        // When
        AwardResponseDTO result = awardService.create(dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(testAwardId);
        assertThat(result.contractNumber()).isEqualTo(testAward.getContractNumber());
        verify(awardRepository).findByContractNumber(dto.contractNumber());
        verify(awardRepository).save(any(Award.class));
    }

    @Test
    void create_ShouldThrowException_WhenContractNumberExists() {
        // Given
        AwardCreateDTO dto = new AwardCreateDTO(
            null,
            "CONTRACT-2024-001",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );

        when(awardRepository.findByContractNumber(dto.contractNumber())).thenReturn(Optional.of(testAward));

        // When & Then
        assertThatThrownBy(() -> awardService.create(dto))
            .isInstanceOf(DuplicateEntityException.class)
            .hasMessageContaining("contractNumber");

        verify(awardRepository).findByContractNumber(dto.contractNumber());
        verify(awardRepository, never()).save(any(Award.class));
    }

    @Test
    void create_ShouldThrowException_WhenOpportunityNotFound() {
        // Given
        UUID nonExistentOpportunityId = UUID.randomUUID();
        AwardCreateDTO dto = new AwardCreateDTO(
            nonExistentOpportunityId,
            "CONTRACT-2024-003",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );

        when(awardRepository.findByContractNumber(dto.contractNumber())).thenReturn(Optional.empty());
        when(opportunityRepository.findById(nonExistentOpportunityId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> awardService.create(dto))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Opportunity");

        verify(opportunityRepository).findById(nonExistentOpportunityId);
        verify(awardRepository, never()).save(any(Award.class));
    }

    @Test
    void findById_ShouldReturnAward_WhenExists() {
        // Given
        when(awardRepository.findById(testAwardId)).thenReturn(Optional.of(testAward));

        // When
        Optional<AwardResponseDTO> result = awardService.findById(testAwardId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(testAwardId);
        assertThat(result.get().contractNumber()).isEqualTo(testAward.getContractNumber());
        verify(awardRepository).findById(testAwardId);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(awardRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When
        Optional<AwardResponseDTO> result = awardService.findById(nonExistentId);

        // Then
        assertThat(result).isEmpty();
        verify(awardRepository).findById(nonExistentId);
    }

    @Test
    void findAll_ShouldReturnPageOfAwards() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Award> awardPage = new PageImpl<>(Arrays.asList(testAward));
        when(awardRepository.findAll(pageable)).thenReturn(awardPage);

        // When
        Page<AwardResponseDTO> result = awardService.findAll(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).id()).isEqualTo(testAwardId);
        verify(awardRepository).findAll(pageable);
    }

    @Test
    void update_ShouldUpdateAward_WhenValidData() {
        // Given
        AwardUpdateDTO dto = new AwardUpdateDTO(
            "Updated Title",
            testOrganizationId,
            "Updated Company",
            "NEWUEI123456",
            null,
            LocalDate.of(2024, 2, 15),
            new BigDecimal("1500000.00"),
            "USD",
            null,
            null,
            testAgencyId,
            "Updated Office",
            null,
            null,
            null,
            "Updated description",
            true
        );

        when(awardRepository.findById(testAwardId)).thenReturn(Optional.of(testAward));
        when(organizationRepository.findById(testOrganizationId)).thenReturn(Optional.of(testOrganization));
        when(agencyRepository.findById(testAgencyId)).thenReturn(Optional.of(testAgency));
        when(awardRepository.save(any(Award.class))).thenReturn(testAward);

        // When
        AwardResponseDTO result = awardService.update(testAwardId, dto);

        // Then
        assertThat(result).isNotNull();
        verify(awardRepository).findById(testAwardId);
        verify(awardRepository).save(any(Award.class));
    }

    @Test
    void update_ShouldThrowException_WhenAwardNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        AwardUpdateDTO dto = new AwardUpdateDTO(
            "Updated Title",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );

        when(awardRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> awardService.update(nonExistentId, dto))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Award");

        verify(awardRepository).findById(nonExistentId);
        verify(awardRepository, never()).save(any(Award.class));
    }

    @Test
    void delete_ShouldSoftDeleteAward_WhenExists() {
        // Given
        when(awardRepository.findById(testAwardId)).thenReturn(Optional.of(testAward));
        when(awardRepository.save(any(Award.class))).thenReturn(testAward);

        // When
        awardService.delete(testAwardId);

        // Then
        verify(awardRepository).findById(testAwardId);
        verify(awardRepository).save(any(Award.class));
    }

    @Test
    void delete_ShouldThrowException_WhenAwardNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(awardRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> awardService.delete(nonExistentId))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Award");

        verify(awardRepository).findById(nonExistentId);
        verify(awardRepository, never()).save(any(Award.class));
    }

    @Test
    void findByContractNumber_ShouldReturnAward_WhenExists() {
        // Given
        String contractNumber = "CONTRACT-2024-001";
        when(awardRepository.findByContractNumber(contractNumber)).thenReturn(Optional.of(testAward));

        // When
        Optional<AwardResponseDTO> result = awardService.findByContractNumber(contractNumber);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().contractNumber()).isEqualTo(contractNumber);
        verify(awardRepository).findByContractNumber(contractNumber);
    }

    @Test
    void findActiveAwards_ShouldReturnListOfActiveAwards() {
        // Given
        List<Award> activeAwards = Arrays.asList(testAward);
        when(awardRepository.findByIsActiveTrue()).thenReturn(activeAwards);

        // When
        List<AwardResponseDTO> result = awardService.findActiveAwards();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).isActive()).isTrue();
        verify(awardRepository).findByIsActiveTrue();
    }

    @Test
    void findByAwardDateBetween_ShouldReturnListOfAwards() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        List<Award> awards = Arrays.asList(testAward);
        when(awardRepository.findByAwardDateBetween(startDate, endDate)).thenReturn(awards);

        // When
        List<AwardResponseDTO> result = awardService.findByAwardDateBetween(startDate, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(awardRepository).findByAwardDateBetween(startDate, endDate);
    }

    @Test
    void findByNaicsCode_ShouldReturnListOfAwards() {
        // Given
        String naicsCode = "541512";
        List<Award> awards = Arrays.asList(testAward);
        when(awardRepository.findByNaicsCode(naicsCode)).thenReturn(awards);

        // When
        List<AwardResponseDTO> result = awardService.findByNaicsCode(naicsCode);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).naicsCode()).isEqualTo(naicsCode);
        verify(awardRepository).findByNaicsCode(naicsCode);
    }

    @Test
    void findByAwardeeUei_ShouldReturnListOfAwards() {
        // Given
        String uei = "ABC123456789";
        List<Award> awards = Arrays.asList(testAward);
        when(awardRepository.findByAwardeeUei(uei)).thenReturn(awards);

        // When
        List<AwardResponseDTO> result = awardService.findByAwardeeUei(uei);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).awardeeUei()).isEqualTo(uei);
        verify(awardRepository).findByAwardeeUei(uei);
    }

    @Test
    void existsByContractNumber_ShouldReturnTrue_WhenExists() {
        // Given
        String contractNumber = "CONTRACT-2024-001";
        when(awardRepository.findByContractNumber(contractNumber)).thenReturn(Optional.of(testAward));

        // When
        boolean result = awardService.existsByContractNumber(contractNumber);

        // Then
        assertThat(result).isTrue();
        verify(awardRepository).findByContractNumber(contractNumber);
    }

    @Test
    void existsByContractNumber_ShouldReturnFalse_WhenNotExists() {
        // Given
        String contractNumber = "NON-EXISTENT";
        when(awardRepository.findByContractNumber(contractNumber)).thenReturn(Optional.empty());

        // When
        boolean result = awardService.existsByContractNumber(contractNumber);

        // Then
        assertThat(result).isFalse();
        verify(awardRepository).findByContractNumber(contractNumber);
    }
}
