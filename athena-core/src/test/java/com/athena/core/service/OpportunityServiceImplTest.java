package com.athena.core.service;

import com.athena.core.dto.OpportunityCreateDTO;
import com.athena.core.dto.OpportunityResponseDTO;
import com.athena.core.entity.Agency;
import com.athena.core.entity.Opportunity;
import com.athena.core.exception.DuplicateEntityException;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.AgencyRepository;
import com.athena.core.repository.OpportunityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class OpportunityServiceImplTest {

    @Mock
    private OpportunityRepository opportunityRepository;

    @Mock
    private AgencyRepository agencyRepository;

    @InjectMocks
    private OpportunityServiceImpl opportunityService;

    private Opportunity testOpportunity;
    private UUID testOppId;

    @BeforeEach
    void setUp() {
        testOppId = UUID.randomUUID();
        testOpportunity = new Opportunity("NOTICE-123", "Test Opportunity", "Solicitation");
        testOpportunity.setId(testOppId);
        testOpportunity.setNaicsCode("541512");
        testOpportunity.setIsActive(true);
        testOpportunity.setPostedDate(LocalDate.now());
    }

    @Test
    void create_ShouldCreateOpportunity_WhenValidData() {
        // Given
        OpportunityCreateDTO dto = new OpportunityCreateDTO(
            "NOTICE-456",
            "New Opportunity",
            null,
            null,
            null,
            "Presolicitation",
            null,
            null,
            null,
            "541512",
            null,
            null,
            LocalDate.now(),
            Instant.now().plusSeconds(86400),
            "Description",
            null,
            null,
            null,
            null,
            null,
            null,
            "US",
            true
        );

        when(opportunityRepository.existsByNoticeId(dto.noticeId())).thenReturn(false);
        when(opportunityRepository.save(any(Opportunity.class))).thenReturn(testOpportunity);

        // When
        OpportunityResponseDTO result = opportunityService.create(dto);

        // Then
        assertThat(result).isNotNull();
        verify(opportunityRepository).existsByNoticeId(dto.noticeId());
        verify(opportunityRepository).save(any(Opportunity.class));
    }

    @Test
    void create_ShouldThrowException_WhenNoticeIdExists() {
        // Given
        OpportunityCreateDTO dto = new OpportunityCreateDTO(
            "NOTICE-123",
            "Duplicate Opportunity",
            null,
            null,
            null,
            "Solicitation",
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
            null,
            null
        );

        when(opportunityRepository.existsByNoticeId(dto.noticeId())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> opportunityService.create(dto))
            .isInstanceOf(DuplicateEntityException.class)
            .hasMessageContaining("noticeId");

        verify(opportunityRepository).existsByNoticeId(dto.noticeId());
        verify(opportunityRepository, never()).save(any(Opportunity.class));
    }

    @Test
    void findByNoticeId_ShouldReturnOpportunity_WhenExists() {
        // Given
        when(opportunityRepository.findByNoticeId("NOTICE-123"))
            .thenReturn(Optional.of(testOpportunity));

        // When
        Optional<OpportunityResponseDTO> result = opportunityService.findByNoticeId("NOTICE-123");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().noticeId()).isEqualTo("NOTICE-123");
        verify(opportunityRepository).findByNoticeId("NOTICE-123");
    }

    @Test
    void findActiveOpportunities_ShouldReturnActiveOpportunities() {
        // Given
        when(opportunityRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(testOpportunity));

        // When
        List<OpportunityResponseDTO> result = opportunityService.findActiveOpportunities();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).isActive()).isTrue();
        verify(opportunityRepository).findByIsActiveTrue();
    }

    @Test
    void findByNaicsCode_ShouldReturnMatchingOpportunities() {
        // Given
        when(opportunityRepository.findByNaicsCode("541512"))
            .thenReturn(Arrays.asList(testOpportunity));

        // When
        List<OpportunityResponseDTO> result = opportunityService.findByNaicsCode("541512");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).naicsCode()).isEqualTo("541512");
        verify(opportunityRepository).findByNaicsCode("541512");
    }

    @Test
    void findUpcomingDeadlines_ShouldReturnOpportunitiesWithUpcomingDeadlines() {
        // Given
        when(opportunityRepository.findActiveOpportunitiesWithUpcomingDeadlines(any(Instant.class), any(Instant.class)))
            .thenReturn(Arrays.asList(testOpportunity));

        // When
        List<OpportunityResponseDTO> result = opportunityService.findUpcomingDeadlines(7);

        // Then
        assertThat(result).hasSize(1);
        verify(opportunityRepository).findActiveOpportunitiesWithUpcomingDeadlines(any(Instant.class), any(Instant.class));
    }

    @Test
    void delete_ShouldSoftDeleteOpportunity_WhenExists() {
        // Given
        when(opportunityRepository.findById(testOppId)).thenReturn(Optional.of(testOpportunity));
        when(opportunityRepository.save(any(Opportunity.class))).thenReturn(testOpportunity);

        // When
        opportunityService.delete(testOppId);

        // Then
        verify(opportunityRepository).findById(testOppId);
        verify(opportunityRepository).save(testOpportunity);
        assertThat(testOpportunity.getIsActive()).isFalse();
    }
}
