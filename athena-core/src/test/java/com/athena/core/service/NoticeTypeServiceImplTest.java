package com.athena.core.service;

import com.athena.core.dto.NoticeTypeCreateDTO;
import com.athena.core.dto.NoticeTypeResponseDTO;
import com.athena.core.dto.NoticeTypeUpdateDTO;
import com.athena.core.entity.NoticeType;
import com.athena.core.exception.DuplicateEntityException;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.NoticeTypeRepository;
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
class NoticeTypeServiceImplTest {

    @Mock
    private NoticeTypeRepository noticeTypeRepository;

    @InjectMocks
    private NoticeTypeServiceImpl noticeTypeService;

    private NoticeType testNoticeType;
    private UUID testNoticeTypeId;

    @BeforeEach
    void setUp() {
        testNoticeTypeId = UUID.randomUUID();
        testNoticeType = new NoticeType("PRESOL", "Presolicitation");
        testNoticeType.setId(testNoticeTypeId);
        testNoticeType.setDescription("Notice of intent to issue a solicitation");
        testNoticeType.setCategory("Pre-Award");
        testNoticeType.setIsActive(true);
        testNoticeType.setCreatedAt(Instant.now());
        testNoticeType.setUpdatedAt(Instant.now());
    }

    @Test
    void create_ShouldCreateNoticeType_WhenValidData() {
        // Given
        NoticeTypeCreateDTO dto = new NoticeTypeCreateDTO(
            "AWARD",
            "Award Notice",
            "Notice of contract award",
            "Post-Award"
        );

        when(noticeTypeRepository.existsByCode(dto.code())).thenReturn(false);
        when(noticeTypeRepository.save(any(NoticeType.class))).thenReturn(testNoticeType);

        // When
        NoticeTypeResponseDTO result = noticeTypeService.create(dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.code()).isEqualTo(testNoticeType.getCode());
        verify(noticeTypeRepository).existsByCode(dto.code());
        verify(noticeTypeRepository).save(any(NoticeType.class));
    }

    @Test
    void create_ShouldThrowException_WhenCodeExists() {
        // Given
        NoticeTypeCreateDTO dto = new NoticeTypeCreateDTO(
            "PRESOL",
            "Presolicitation",
            null,
            null
        );

        when(noticeTypeRepository.existsByCode(dto.code())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> noticeTypeService.create(dto))
            .isInstanceOf(DuplicateEntityException.class)
            .hasMessageContaining("code");

        verify(noticeTypeRepository).existsByCode(dto.code());
        verify(noticeTypeRepository, never()).save(any(NoticeType.class));
    }

    @Test
    void findById_ShouldReturnNoticeType_WhenExists() {
        // Given
        when(noticeTypeRepository.findById(testNoticeTypeId)).thenReturn(Optional.of(testNoticeType));

        // When
        Optional<NoticeTypeResponseDTO> result = noticeTypeService.findById(testNoticeTypeId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(testNoticeTypeId);
        assertThat(result.get().code()).isEqualTo("PRESOL");
        verify(noticeTypeRepository).findById(testNoticeTypeId);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(noticeTypeRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When
        Optional<NoticeTypeResponseDTO> result = noticeTypeService.findById(nonExistentId);

        // Then
        assertThat(result).isEmpty();
        verify(noticeTypeRepository).findById(nonExistentId);
    }

    @Test
    void findAll_ShouldReturnPageOfNoticeTypes() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<NoticeType> noticeTypePage = new PageImpl<>(Arrays.asList(testNoticeType));
        when(noticeTypeRepository.findAll(pageable)).thenReturn(noticeTypePage);

        // When
        Page<NoticeTypeResponseDTO> result = noticeTypeService.findAll(pageable);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).id()).isEqualTo(testNoticeTypeId);
        verify(noticeTypeRepository).findAll(pageable);
    }

    @Test
    void update_ShouldUpdateNoticeType_WhenValidData() {
        // Given
        NoticeTypeUpdateDTO dto = new NoticeTypeUpdateDTO(
            "Updated Name",
            "Updated description",
            "Updated Category",
            null
        );

        when(noticeTypeRepository.findById(testNoticeTypeId)).thenReturn(Optional.of(testNoticeType));
        when(noticeTypeRepository.save(any(NoticeType.class))).thenReturn(testNoticeType);

        // When
        NoticeTypeResponseDTO result = noticeTypeService.update(testNoticeTypeId, dto);

        // Then
        assertThat(result).isNotNull();
        verify(noticeTypeRepository).findById(testNoticeTypeId);
        verify(noticeTypeRepository).save(testNoticeType);
    }

    @Test
    void update_ShouldThrowException_WhenNoticeTypeNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        NoticeTypeUpdateDTO dto = new NoticeTypeUpdateDTO("New Name", null, null, null);

        when(noticeTypeRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> noticeTypeService.update(nonExistentId, dto))
            .isInstanceOf(EntityNotFoundException.class);

        verify(noticeTypeRepository).findById(nonExistentId);
        verify(noticeTypeRepository, never()).save(any(NoticeType.class));
    }

    @Test
    void delete_ShouldSoftDeleteNoticeType_WhenExists() {
        // Given
        when(noticeTypeRepository.findById(testNoticeTypeId)).thenReturn(Optional.of(testNoticeType));
        when(noticeTypeRepository.save(any(NoticeType.class))).thenReturn(testNoticeType);

        // When
        noticeTypeService.delete(testNoticeTypeId);

        // Then
        verify(noticeTypeRepository).findById(testNoticeTypeId);
        verify(noticeTypeRepository).save(testNoticeType);
        assertThat(testNoticeType.getIsActive()).isFalse();
    }

    @Test
    void delete_ShouldThrowException_WhenNoticeTypeNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(noticeTypeRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> noticeTypeService.delete(nonExistentId))
            .isInstanceOf(EntityNotFoundException.class);

        verify(noticeTypeRepository).findById(nonExistentId);
        verify(noticeTypeRepository, never()).save(any(NoticeType.class));
    }

    @Test
    void findByCode_ShouldReturnNoticeType_WhenExists() {
        // Given
        String code = "PRESOL";
        when(noticeTypeRepository.findByCode(code)).thenReturn(Optional.of(testNoticeType));

        // When
        Optional<NoticeTypeResponseDTO> result = noticeTypeService.findByCode(code);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().code()).isEqualTo(code);
        verify(noticeTypeRepository).findByCode(code);
    }

    @Test
    void existsByCode_ShouldReturnTrue_WhenExists() {
        // Given
        String code = "PRESOL";
        when(noticeTypeRepository.existsByCode(code)).thenReturn(true);

        // When
        boolean result = noticeTypeService.existsByCode(code);

        // Then
        assertThat(result).isTrue();
        verify(noticeTypeRepository).existsByCode(code);
    }
}
