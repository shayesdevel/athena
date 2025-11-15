package com.athena.core.service;

import com.athena.core.dto.AgencyCreateDTO;
import com.athena.core.dto.AgencyResponseDTO;
import com.athena.core.entity.Agency;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.AgencyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgencyServiceImplTest {

    @Mock
    private AgencyRepository agencyRepository;

    @InjectMocks
    private AgencyServiceImpl agencyService;

    private Agency testAgency;
    private UUID testAgencyId;

    @BeforeEach
    void setUp() {
        testAgencyId = UUID.randomUUID();
        testAgency = new Agency("Department of Defense", "DOD");
        testAgency.setId(testAgencyId);
        testAgency.setDepartment("Defense");
        testAgency.setIsActive(true);
    }

    @Test
    void create_ShouldCreateAgency_WhenValidData() {
        // Given
        AgencyCreateDTO dto = new AgencyCreateDTO(
            "NASA",
            "NASA",
            null,
            "Aeronautics and Space",
            "Cabinet",
            true
        );

        when(agencyRepository.save(any(Agency.class))).thenReturn(testAgency);

        // When
        AgencyResponseDTO result = agencyService.create(dto);

        // Then
        assertThat(result).isNotNull();
        verify(agencyRepository).save(any(Agency.class));
    }

    @Test
    void create_ShouldSetParentAgency_WhenProvided() {
        // Given
        UUID parentId = UUID.randomUUID();
        Agency parentAgency = new Agency("Parent Agency", "PA");
        AgencyCreateDTO dto = new AgencyCreateDTO(
            "Sub Agency",
            "SA",
            parentId,
            null,
            null,
            true
        );

        when(agencyRepository.findById(parentId)).thenReturn(Optional.of(parentAgency));
        when(agencyRepository.save(any(Agency.class))).thenReturn(testAgency);

        // When
        AgencyResponseDTO result = agencyService.create(dto);

        // Then
        assertThat(result).isNotNull();
        verify(agencyRepository).findById(parentId);
        verify(agencyRepository).save(any(Agency.class));
    }

    @Test
    void findById_ShouldReturnAgency_WhenExists() {
        // Given
        when(agencyRepository.findById(testAgencyId)).thenReturn(Optional.of(testAgency));

        // When
        Optional<AgencyResponseDTO> result = agencyService.findById(testAgencyId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(testAgencyId);
        verify(agencyRepository).findById(testAgencyId);
    }

    @Test
    void findByAbbreviation_ShouldReturnAgency_WhenExists() {
        // Given
        when(agencyRepository.findByAbbreviation("DOD")).thenReturn(Optional.of(testAgency));

        // When
        Optional<AgencyResponseDTO> result = agencyService.findByAbbreviation("DOD");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().abbreviation()).isEqualTo("DOD");
        verify(agencyRepository).findByAbbreviation("DOD");
    }

    @Test
    void findActiveAgencies_ShouldReturnActiveAgencies() {
        // Given
        when(agencyRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(testAgency));

        // When
        List<AgencyResponseDTO> result = agencyService.findActiveAgencies();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).isActive()).isTrue();
        verify(agencyRepository).findByIsActiveTrue();
    }

    @Test
    void findSubAgencies_ShouldReturnSubAgencies() {
        // Given
        UUID parentId = UUID.randomUUID();
        when(agencyRepository.findByParentAgencyId(parentId)).thenReturn(Arrays.asList(testAgency));

        // When
        List<AgencyResponseDTO> result = agencyService.findSubAgencies(parentId);

        // Then
        assertThat(result).hasSize(1);
        verify(agencyRepository).findByParentAgencyId(parentId);
    }

    @Test
    void delete_ShouldSoftDeleteAgency_WhenExists() {
        // Given
        when(agencyRepository.findById(testAgencyId)).thenReturn(Optional.of(testAgency));
        when(agencyRepository.save(any(Agency.class))).thenReturn(testAgency);

        // When
        agencyService.delete(testAgencyId);

        // Then
        verify(agencyRepository).findById(testAgencyId);
        verify(agencyRepository).save(testAgency);
        assertThat(testAgency.getIsActive()).isFalse();
    }

    @Test
    void delete_ShouldThrowException_WhenNotExists() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(agencyRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> agencyService.delete(nonExistentId))
            .isInstanceOf(EntityNotFoundException.class);

        verify(agencyRepository).findById(nonExistentId);
        verify(agencyRepository, never()).save(any(Agency.class));
    }
}
