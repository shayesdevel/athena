package com.athena.core.service;

import com.athena.core.dto.OrganizationCreateDTO;
import com.athena.core.dto.OrganizationResponseDTO;
import com.athena.core.dto.OrganizationUpdateDTO;
import com.athena.core.entity.Organization;
import com.athena.core.exception.DuplicateEntityException;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.OrganizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceImplTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @InjectMocks
    private OrganizationServiceImpl organizationService;

    private Organization testOrganization;
    private UUID testOrgId;

    @BeforeEach
    void setUp() {
        testOrgId = UUID.randomUUID();
        testOrganization = new Organization("Test Company Inc");
        testOrganization.setId(testOrgId);
        testOrganization.setUei("ABC123456789");
        testOrganization.setCageCode("12345");
        testOrganization.setPrimaryNaics("541512");
        testOrganization.setIsSmallBusiness(true);
        testOrganization.setCreatedAt(Instant.now());
        testOrganization.setUpdatedAt(Instant.now());
    }

    @Test
    void create_ShouldCreateOrganization_WhenValidData() {
        // Given
        OrganizationCreateDTO dto = new OrganizationCreateDTO(
            "New Company",
            "XYZ987654321",
            "54321",
            null,
            null,
            "541512",
            null,
            true,
            false,
            false,
            false,
            null,
            null,
            null,
            null,
            "US",
            null,
            null
        );

        when(organizationRepository.existsByUei(dto.uei())).thenReturn(false);
        when(organizationRepository.save(any(Organization.class))).thenReturn(testOrganization);

        // When
        OrganizationResponseDTO result = organizationService.create(dto);

        // Then
        assertThat(result).isNotNull();
        verify(organizationRepository).existsByUei(dto.uei());
        verify(organizationRepository).save(any(Organization.class));
    }

    @Test
    void create_ShouldThrowException_WhenUeiExists() {
        // Given
        OrganizationCreateDTO dto = new OrganizationCreateDTO(
            "New Company",
            "ABC123456789",
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

        when(organizationRepository.existsByUei(dto.uei())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> organizationService.create(dto))
            .isInstanceOf(DuplicateEntityException.class)
            .hasMessageContaining("UEI");

        verify(organizationRepository).existsByUei(dto.uei());
        verify(organizationRepository, never()).save(any(Organization.class));
    }

    @Test
    void findById_ShouldReturnOrganization_WhenExists() {
        // Given
        when(organizationRepository.findById(testOrgId)).thenReturn(Optional.of(testOrganization));

        // When
        Optional<OrganizationResponseDTO> result = organizationService.findById(testOrgId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(testOrgId);
        verify(organizationRepository).findById(testOrgId);
    }

    @Test
    void findByUei_ShouldReturnOrganization_WhenExists() {
        // Given
        String uei = "ABC123456789";
        when(organizationRepository.findByUei(uei)).thenReturn(Optional.of(testOrganization));

        // When
        Optional<OrganizationResponseDTO> result = organizationService.findByUei(uei);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().uei()).isEqualTo(uei);
        verify(organizationRepository).findByUei(uei);
    }

    @Test
    void searchByName_ShouldReturnMatchingOrganizations() {
        // Given
        String searchTerm = "Test";
        when(organizationRepository.findByNameContainingIgnoreCase(searchTerm))
            .thenReturn(Arrays.asList(testOrganization));

        // When
        List<OrganizationResponseDTO> result = organizationService.searchByName(searchTerm);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).contains("Test");
        verify(organizationRepository).findByNameContainingIgnoreCase(searchTerm);
    }

    @Test
    void findSmallBusinesses_ShouldReturnSmallBusinessOrganizations() {
        // Given
        when(organizationRepository.findByIsSmallBusinessTrue())
            .thenReturn(Arrays.asList(testOrganization));

        // When
        List<OrganizationResponseDTO> result = organizationService.findSmallBusinesses();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).isSmallBusiness()).isTrue();
        verify(organizationRepository).findByIsSmallBusinessTrue();
    }

    @Test
    void update_ShouldUpdateOrganization_WhenValidData() {
        // Given
        OrganizationUpdateDTO dto = new OrganizationUpdateDTO(
            "Updated Company Name",
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

        when(organizationRepository.findById(testOrgId)).thenReturn(Optional.of(testOrganization));
        when(organizationRepository.save(any(Organization.class))).thenReturn(testOrganization);

        // When
        OrganizationResponseDTO result = organizationService.update(testOrgId, dto);

        // Then
        assertThat(result).isNotNull();
        verify(organizationRepository).findById(testOrgId);
        verify(organizationRepository).save(testOrganization);
    }

    @Test
    void delete_ShouldDeleteOrganization_WhenExists() {
        // Given
        when(organizationRepository.existsById(testOrgId)).thenReturn(true);

        // When
        organizationService.delete(testOrgId);

        // Then
        verify(organizationRepository).existsById(testOrgId);
        verify(organizationRepository).deleteById(testOrgId);
    }

    @Test
    void delete_ShouldThrowException_WhenNotExists() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(organizationRepository.existsById(nonExistentId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> organizationService.delete(nonExistentId))
            .isInstanceOf(EntityNotFoundException.class);

        verify(organizationRepository).existsById(nonExistentId);
        verify(organizationRepository, never()).deleteById(any());
    }
}
