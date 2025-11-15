package com.athena.core.service;

import com.athena.core.dto.SetAsideCreateDTO;
import com.athena.core.dto.SetAsideResponseDTO;
import com.athena.core.dto.SetAsideUpdateDTO;
import com.athena.core.entity.SetAside;
import com.athena.core.exception.DuplicateEntityException;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.SetAsideRepository;
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
class SetAsideServiceImplTest {

    @Mock
    private SetAsideRepository setAsideRepository;

    @InjectMocks
    private SetAsideServiceImpl setAsideService;

    private SetAside testSetAside;
    private UUID testSetAsideId;

    @BeforeEach
    void setUp() {
        testSetAsideId = UUID.randomUUID();
        testSetAside = new SetAside("SBA", "Small Business");
        testSetAside.setId(testSetAsideId);
        testSetAside.setDescription("Small business set-aside");
        testSetAside.setEligibilityCriteria("Must be certified small business");
        testSetAside.setIsActive(true);
        testSetAside.setCreatedAt(Instant.now());
        testSetAside.setUpdatedAt(Instant.now());
    }

    @Test
    void create_ShouldCreateSetAside_WhenValidData() {
        // Given
        SetAsideCreateDTO dto = new SetAsideCreateDTO(
            "8A",
            "8(a) Business Development",
            "8(a) program for disadvantaged businesses",
            "SBA 8(a) certified"
        );

        when(setAsideRepository.existsByCode(dto.code())).thenReturn(false);
        when(setAsideRepository.save(any(SetAside.class))).thenReturn(testSetAside);

        // When
        SetAsideResponseDTO result = setAsideService.create(dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.code()).isEqualTo(testSetAside.getCode());
        verify(setAsideRepository).existsByCode(dto.code());
        verify(setAsideRepository).save(any(SetAside.class));
    }

    @Test
    void create_ShouldThrowException_WhenCodeExists() {
        // Given
        SetAsideCreateDTO dto = new SetAsideCreateDTO(
            "SBA",
            "Small Business",
            null,
            null
        );

        when(setAsideRepository.existsByCode(dto.code())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> setAsideService.create(dto))
            .isInstanceOf(DuplicateEntityException.class)
            .hasMessageContaining("code");

        verify(setAsideRepository).existsByCode(dto.code());
        verify(setAsideRepository, never()).save(any(SetAside.class));
    }

    @Test
    void findById_ShouldReturnSetAside_WhenExists() {
        // Given
        when(setAsideRepository.findById(testSetAsideId)).thenReturn(Optional.of(testSetAside));

        // When
        Optional<SetAsideResponseDTO> result = setAsideService.findById(testSetAsideId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(testSetAsideId);
        assertThat(result.get().code()).isEqualTo("SBA");
        verify(setAsideRepository).findById(testSetAsideId);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(setAsideRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When
        Optional<SetAsideResponseDTO> result = setAsideService.findById(nonExistentId);

        // Then
        assertThat(result).isEmpty();
        verify(setAsideRepository).findById(nonExistentId);
    }

    @Test
    void findAll_ShouldReturnPageOfSetAsides() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<SetAside> setAsidePage = new PageImpl<>(Arrays.asList(testSetAside));
        when(setAsideRepository.findAll(pageable)).thenReturn(setAsidePage);

        // When
        Page<SetAsideResponseDTO> result = setAsideService.findAll(pageable);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).id()).isEqualTo(testSetAsideId);
        verify(setAsideRepository).findAll(pageable);
    }

    @Test
    void update_ShouldUpdateSetAside_WhenValidData() {
        // Given
        SetAsideUpdateDTO dto = new SetAsideUpdateDTO(
            "Updated Name",
            "Updated description",
            "Updated criteria",
            null
        );

        when(setAsideRepository.findById(testSetAsideId)).thenReturn(Optional.of(testSetAside));
        when(setAsideRepository.save(any(SetAside.class))).thenReturn(testSetAside);

        // When
        SetAsideResponseDTO result = setAsideService.update(testSetAsideId, dto);

        // Then
        assertThat(result).isNotNull();
        verify(setAsideRepository).findById(testSetAsideId);
        verify(setAsideRepository).save(testSetAside);
    }

    @Test
    void update_ShouldThrowException_WhenSetAsideNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        SetAsideUpdateDTO dto = new SetAsideUpdateDTO("New Name", null, null, null);

        when(setAsideRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> setAsideService.update(nonExistentId, dto))
            .isInstanceOf(EntityNotFoundException.class);

        verify(setAsideRepository).findById(nonExistentId);
        verify(setAsideRepository, never()).save(any(SetAside.class));
    }

    @Test
    void delete_ShouldSoftDeleteSetAside_WhenExists() {
        // Given
        when(setAsideRepository.findById(testSetAsideId)).thenReturn(Optional.of(testSetAside));
        when(setAsideRepository.save(any(SetAside.class))).thenReturn(testSetAside);

        // When
        setAsideService.delete(testSetAsideId);

        // Then
        verify(setAsideRepository).findById(testSetAsideId);
        verify(setAsideRepository).save(testSetAside);
        assertThat(testSetAside.getIsActive()).isFalse();
    }

    @Test
    void delete_ShouldThrowException_WhenSetAsideNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(setAsideRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> setAsideService.delete(nonExistentId))
            .isInstanceOf(EntityNotFoundException.class);

        verify(setAsideRepository).findById(nonExistentId);
        verify(setAsideRepository, never()).save(any(SetAside.class));
    }

    @Test
    void findByCode_ShouldReturnSetAside_WhenExists() {
        // Given
        String code = "SBA";
        when(setAsideRepository.findByCode(code)).thenReturn(Optional.of(testSetAside));

        // When
        Optional<SetAsideResponseDTO> result = setAsideService.findByCode(code);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().code()).isEqualTo(code);
        verify(setAsideRepository).findByCode(code);
    }

    @Test
    void existsByCode_ShouldReturnTrue_WhenExists() {
        // Given
        String code = "SBA";
        when(setAsideRepository.existsByCode(code)).thenReturn(true);

        // When
        boolean result = setAsideService.existsByCode(code);

        // Then
        assertThat(result).isTrue();
        verify(setAsideRepository).existsByCode(code);
    }
}
