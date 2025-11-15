package com.athena.core.service;

import com.athena.core.dto.NaicsCreateDTO;
import com.athena.core.dto.NaicsResponseDTO;
import com.athena.core.dto.NaicsUpdateDTO;
import com.athena.core.entity.Naics;
import com.athena.core.exception.DuplicateEntityException;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.NaicsRepository;
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
class NaicsServiceImplTest {

    @Mock
    private NaicsRepository naicsRepository;

    @InjectMocks
    private NaicsServiceImpl naicsService;

    private Naics testNaics;
    private UUID testNaicsId;

    @BeforeEach
    void setUp() {
        testNaicsId = UUID.randomUUID();
        testNaics = new Naics("541512", "Computer Systems Design Services");
        testNaics.setId(testNaicsId);
        testNaics.setDescription("Custom computer programming services");
        testNaics.setParentCode("5415");
        testNaics.setLevel(6);
        testNaics.setYearVersion("2022");
        testNaics.setIsActive(true);
        testNaics.setCreatedAt(Instant.now());
        testNaics.setUpdatedAt(Instant.now());
    }

    @Test
    void create_ShouldCreateNaics_WhenValidData() {
        // Given
        NaicsCreateDTO dto = new NaicsCreateDTO(
            "541511",
            "Custom Computer Programming Services",
            "Writing, modifying, testing computer programs",
            "5415",
            6,
            "2022"
        );

        when(naicsRepository.existsByCode(dto.code())).thenReturn(false);
        when(naicsRepository.save(any(Naics.class))).thenReturn(testNaics);

        // When
        NaicsResponseDTO result = naicsService.create(dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.code()).isEqualTo(testNaics.getCode());
        verify(naicsRepository).existsByCode(dto.code());
        verify(naicsRepository).save(any(Naics.class));
    }

    @Test
    void create_ShouldThrowException_WhenCodeExists() {
        // Given
        NaicsCreateDTO dto = new NaicsCreateDTO(
            "541512",
            "Computer Systems Design Services",
            null,
            null,
            6,
            null
        );

        when(naicsRepository.existsByCode(dto.code())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> naicsService.create(dto))
            .isInstanceOf(DuplicateEntityException.class)
            .hasMessageContaining("code");

        verify(naicsRepository).existsByCode(dto.code());
        verify(naicsRepository, never()).save(any(Naics.class));
    }

    @Test
    void findById_ShouldReturnNaics_WhenExists() {
        // Given
        when(naicsRepository.findById(testNaicsId)).thenReturn(Optional.of(testNaics));

        // When
        Optional<NaicsResponseDTO> result = naicsService.findById(testNaicsId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(testNaicsId);
        assertThat(result.get().code()).isEqualTo("541512");
        verify(naicsRepository).findById(testNaicsId);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(naicsRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When
        Optional<NaicsResponseDTO> result = naicsService.findById(nonExistentId);

        // Then
        assertThat(result).isEmpty();
        verify(naicsRepository).findById(nonExistentId);
    }

    @Test
    void findAll_ShouldReturnPageOfNaics() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Naics> naicsPage = new PageImpl<>(Arrays.asList(testNaics));
        when(naicsRepository.findAll(pageable)).thenReturn(naicsPage);

        // When
        Page<NaicsResponseDTO> result = naicsService.findAll(pageable);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).id()).isEqualTo(testNaicsId);
        verify(naicsRepository).findAll(pageable);
    }

    @Test
    void update_ShouldUpdateNaics_WhenValidData() {
        // Given
        NaicsUpdateDTO dto = new NaicsUpdateDTO(
            "Updated Title",
            "Updated description",
            "5416",
            6,
            "2023",
            null
        );

        when(naicsRepository.findById(testNaicsId)).thenReturn(Optional.of(testNaics));
        when(naicsRepository.save(any(Naics.class))).thenReturn(testNaics);

        // When
        NaicsResponseDTO result = naicsService.update(testNaicsId, dto);

        // Then
        assertThat(result).isNotNull();
        verify(naicsRepository).findById(testNaicsId);
        verify(naicsRepository).save(testNaics);
    }

    @Test
    void update_ShouldThrowException_WhenNaicsNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        NaicsUpdateDTO dto = new NaicsUpdateDTO("New Title", null, null, null, null, null);

        when(naicsRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> naicsService.update(nonExistentId, dto))
            .isInstanceOf(EntityNotFoundException.class);

        verify(naicsRepository).findById(nonExistentId);
        verify(naicsRepository, never()).save(any(Naics.class));
    }

    @Test
    void delete_ShouldSoftDeleteNaics_WhenExists() {
        // Given
        when(naicsRepository.findById(testNaicsId)).thenReturn(Optional.of(testNaics));
        when(naicsRepository.save(any(Naics.class))).thenReturn(testNaics);

        // When
        naicsService.delete(testNaicsId);

        // Then
        verify(naicsRepository).findById(testNaicsId);
        verify(naicsRepository).save(testNaics);
        assertThat(testNaics.getIsActive()).isFalse();
    }

    @Test
    void delete_ShouldThrowException_WhenNaicsNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(naicsRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> naicsService.delete(nonExistentId))
            .isInstanceOf(EntityNotFoundException.class);

        verify(naicsRepository).findById(nonExistentId);
        verify(naicsRepository, never()).save(any(Naics.class));
    }

    @Test
    void findByCode_ShouldReturnNaics_WhenExists() {
        // Given
        String code = "541512";
        when(naicsRepository.findByCode(code)).thenReturn(Optional.of(testNaics));

        // When
        Optional<NaicsResponseDTO> result = naicsService.findByCode(code);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().code()).isEqualTo(code);
        verify(naicsRepository).findByCode(code);
    }

    @Test
    void existsByCode_ShouldReturnTrue_WhenExists() {
        // Given
        String code = "541512";
        when(naicsRepository.existsByCode(code)).thenReturn(true);

        // When
        boolean result = naicsService.existsByCode(code);

        // Then
        assertThat(result).isTrue();
        verify(naicsRepository).existsByCode(code);
    }
}
