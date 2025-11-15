package com.athena.core.service;

import com.athena.core.dto.ContractVehicleCreateDTO;
import com.athena.core.dto.ContractVehicleResponseDTO;
import com.athena.core.dto.ContractVehicleUpdateDTO;
import com.athena.core.entity.ContractVehicle;
import com.athena.core.exception.DuplicateEntityException;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.ContractVehicleRepository;
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

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractVehicleServiceImplTest {

    @Mock
    private ContractVehicleRepository contractVehicleRepository;

    @InjectMocks
    private ContractVehicleServiceImpl contractVehicleService;

    private ContractVehicle testContractVehicle;
    private UUID testContractVehicleId;

    @BeforeEach
    void setUp() {
        testContractVehicleId = UUID.randomUUID();
        testContractVehicle = new ContractVehicle("GSA_SCHED", "GSA Schedule");
        testContractVehicle.setId(testContractVehicleId);
        testContractVehicle.setDescription("General Services Administration Schedule contract");
        testContractVehicle.setCategory("Schedule");
        testContractVehicle.setManagingAgency("GSA");
        testContractVehicle.setUrl("https://www.gsaelibrary.gsa.gov");
        testContractVehicle.setIsActive(true);
        testContractVehicle.setCreatedAt(Instant.now());
        testContractVehicle.setUpdatedAt(Instant.now());
    }

    @Test
    void create_ShouldCreateContractVehicle_WhenValidData() {
        // Given
        ContractVehicleCreateDTO dto = new ContractVehicleCreateDTO(
            "IDIQ",
            "Indefinite Delivery Indefinite Quantity",
            "Contract for indefinite quantities over time",
            "IDIQ",
            "DOD",
            "https://www.acq.osd.mil"
        );

        when(contractVehicleRepository.existsByCode(dto.code())).thenReturn(false);
        when(contractVehicleRepository.save(any(ContractVehicle.class))).thenReturn(testContractVehicle);

        // When
        ContractVehicleResponseDTO result = contractVehicleService.create(dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.code()).isEqualTo(testContractVehicle.getCode());
        verify(contractVehicleRepository).existsByCode(dto.code());
        verify(contractVehicleRepository).save(any(ContractVehicle.class));
    }

    @Test
    void create_ShouldThrowException_WhenCodeExists() {
        // Given
        ContractVehicleCreateDTO dto = new ContractVehicleCreateDTO(
            "GSA_SCHED",
            "GSA Schedule",
            null,
            null,
            null,
            null
        );

        when(contractVehicleRepository.existsByCode(dto.code())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> contractVehicleService.create(dto))
            .isInstanceOf(DuplicateEntityException.class)
            .hasMessageContaining("code");

        verify(contractVehicleRepository).existsByCode(dto.code());
        verify(contractVehicleRepository, never()).save(any(ContractVehicle.class));
    }

    @Test
    void findById_ShouldReturnContractVehicle_WhenExists() {
        // Given
        when(contractVehicleRepository.findById(testContractVehicleId)).thenReturn(Optional.of(testContractVehicle));

        // When
        Optional<ContractVehicleResponseDTO> result = contractVehicleService.findById(testContractVehicleId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(testContractVehicleId);
        assertThat(result.get().code()).isEqualTo("GSA_SCHED");
        verify(contractVehicleRepository).findById(testContractVehicleId);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(contractVehicleRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When
        Optional<ContractVehicleResponseDTO> result = contractVehicleService.findById(nonExistentId);

        // Then
        assertThat(result).isEmpty();
        verify(contractVehicleRepository).findById(nonExistentId);
    }

    @Test
    void findAll_ShouldReturnPageOfContractVehicles() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<ContractVehicle> contractVehiclePage = new PageImpl<>(Arrays.asList(testContractVehicle));
        when(contractVehicleRepository.findAll(pageable)).thenReturn(contractVehiclePage);

        // When
        Page<ContractVehicleResponseDTO> result = contractVehicleService.findAll(pageable);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).id()).isEqualTo(testContractVehicleId);
        verify(contractVehicleRepository).findAll(pageable);
    }

    @Test
    void update_ShouldUpdateContractVehicle_WhenValidData() {
        // Given
        ContractVehicleUpdateDTO dto = new ContractVehicleUpdateDTO(
            "Updated Name",
            "Updated description",
            "Updated Category",
            "Updated Agency",
            "https://updated.url",
            null
        );

        when(contractVehicleRepository.findById(testContractVehicleId)).thenReturn(Optional.of(testContractVehicle));
        when(contractVehicleRepository.save(any(ContractVehicle.class))).thenReturn(testContractVehicle);

        // When
        ContractVehicleResponseDTO result = contractVehicleService.update(testContractVehicleId, dto);

        // Then
        assertThat(result).isNotNull();
        verify(contractVehicleRepository).findById(testContractVehicleId);
        verify(contractVehicleRepository).save(testContractVehicle);
    }

    @Test
    void update_ShouldThrowException_WhenContractVehicleNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        ContractVehicleUpdateDTO dto = new ContractVehicleUpdateDTO("New Name", null, null, null, null, null);

        when(contractVehicleRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> contractVehicleService.update(nonExistentId, dto))
            .isInstanceOf(EntityNotFoundException.class);

        verify(contractVehicleRepository).findById(nonExistentId);
        verify(contractVehicleRepository, never()).save(any(ContractVehicle.class));
    }

    @Test
    void delete_ShouldSoftDeleteContractVehicle_WhenExists() {
        // Given
        when(contractVehicleRepository.findById(testContractVehicleId)).thenReturn(Optional.of(testContractVehicle));
        when(contractVehicleRepository.save(any(ContractVehicle.class))).thenReturn(testContractVehicle);

        // When
        contractVehicleService.delete(testContractVehicleId);

        // Then
        verify(contractVehicleRepository).findById(testContractVehicleId);
        verify(contractVehicleRepository).save(testContractVehicle);
        assertThat(testContractVehicle.getIsActive()).isFalse();
    }

    @Test
    void delete_ShouldThrowException_WhenContractVehicleNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(contractVehicleRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> contractVehicleService.delete(nonExistentId))
            .isInstanceOf(EntityNotFoundException.class);

        verify(contractVehicleRepository).findById(nonExistentId);
        verify(contractVehicleRepository, never()).save(any(ContractVehicle.class));
    }

    @Test
    void findByCode_ShouldReturnContractVehicle_WhenExists() {
        // Given
        String code = "GSA_SCHED";
        when(contractVehicleRepository.findByCode(code)).thenReturn(Optional.of(testContractVehicle));

        // When
        Optional<ContractVehicleResponseDTO> result = contractVehicleService.findByCode(code);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().code()).isEqualTo(code);
        verify(contractVehicleRepository).findByCode(code);
    }

    @Test
    void existsByCode_ShouldReturnTrue_WhenExists() {
        // Given
        String code = "GSA_SCHED";
        when(contractVehicleRepository.existsByCode(code)).thenReturn(true);

        // When
        boolean result = contractVehicleService.existsByCode(code);

        // Then
        assertThat(result).isTrue();
        verify(contractVehicleRepository).existsByCode(code);
    }
}
