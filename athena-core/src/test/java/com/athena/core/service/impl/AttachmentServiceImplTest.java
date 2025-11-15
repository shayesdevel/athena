package com.athena.core.service.impl;

import com.athena.core.dto.AttachmentCreateDTO;
import com.athena.core.dto.AttachmentResponseDTO;
import com.athena.core.dto.AttachmentUpdateDTO;
import com.athena.core.entity.Attachment;
import com.athena.core.entity.Opportunity;
import com.athena.core.exception.DuplicateEntityException;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.AttachmentRepository;
import com.athena.core.repository.OpportunityRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceImplTest {

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private OpportunityRepository opportunityRepository;

    @InjectMocks
    private AttachmentServiceImpl attachmentService;

    private Attachment testAttachment;
    private Opportunity testOpportunity;
    private UUID testAttachmentId;
    private UUID testOpportunityId;

    @BeforeEach
    void setUp() {
        testAttachmentId = UUID.randomUUID();
        testOpportunityId = UUID.randomUUID();

        testOpportunity = new Opportunity("NOTICE-001", "Test Opportunity", "Presolicitation");
        testOpportunity.setId(testOpportunityId);

        testAttachment = new Attachment(testOpportunity, "solicitation.pdf", "https://example.com/file.pdf");
        testAttachment.setId(testAttachmentId);
        testAttachment.setType("solicitation");
        testAttachment.setMimeType("application/pdf");
        testAttachment.setFileSize(1024000L);
        testAttachment.setDescription("Main solicitation document");
        testAttachment.setSamAttachmentId("SAM-ATT-001");
        testAttachment.setCreatedAt(Instant.now());
        testAttachment.setUpdatedAt(Instant.now());
    }

    @Test
    void create_ShouldCreateAttachment_WhenValidData() {
        // Given
        AttachmentCreateDTO dto = new AttachmentCreateDTO(
            testOpportunityId,
            "amendment.pdf",
            "https://example.com/amendment.pdf",
            "amendment",
            "application/pdf",
            512000L,
            "Amendment 001",
            "SAM-ATT-002"
        );

        when(opportunityRepository.findById(testOpportunityId)).thenReturn(Optional.of(testOpportunity));
        when(attachmentRepository.findBySamAttachmentId(dto.samAttachmentId())).thenReturn(Optional.empty());
        when(attachmentRepository.save(any(Attachment.class))).thenReturn(testAttachment);

        // When
        AttachmentResponseDTO result = attachmentService.create(dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(testAttachmentId);
        assertThat(result.opportunityId()).isEqualTo(testOpportunityId);
        verify(opportunityRepository).findById(testOpportunityId);
        verify(attachmentRepository).findBySamAttachmentId(dto.samAttachmentId());
        verify(attachmentRepository).save(any(Attachment.class));
    }

    @Test
    void create_ShouldThrowException_WhenOpportunityNotFound() {
        // Given
        UUID nonExistentOpportunityId = UUID.randomUUID();
        AttachmentCreateDTO dto = new AttachmentCreateDTO(
            nonExistentOpportunityId,
            "file.pdf",
            "https://example.com/file.pdf",
            "solicitation",
            "application/pdf",
            1024L,
            null,
            null
        );

        when(opportunityRepository.findById(nonExistentOpportunityId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> attachmentService.create(dto))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Opportunity");

        verify(opportunityRepository).findById(nonExistentOpportunityId);
        verify(attachmentRepository, never()).save(any(Attachment.class));
    }

    @Test
    void create_ShouldThrowException_WhenSamAttachmentIdExists() {
        // Given
        AttachmentCreateDTO dto = new AttachmentCreateDTO(
            testOpportunityId,
            "file.pdf",
            "https://example.com/file.pdf",
            "solicitation",
            "application/pdf",
            1024L,
            null,
            "SAM-ATT-001"
        );

        when(opportunityRepository.findById(testOpportunityId)).thenReturn(Optional.of(testOpportunity));
        when(attachmentRepository.findBySamAttachmentId(dto.samAttachmentId()))
            .thenReturn(Optional.of(testAttachment));

        // When & Then
        assertThatThrownBy(() -> attachmentService.create(dto))
            .isInstanceOf(DuplicateEntityException.class)
            .hasMessageContaining("samAttachmentId");

        verify(opportunityRepository).findById(testOpportunityId);
        verify(attachmentRepository).findBySamAttachmentId(dto.samAttachmentId());
        verify(attachmentRepository, never()).save(any(Attachment.class));
    }

    @Test
    void findById_ShouldReturnAttachment_WhenExists() {
        // Given
        when(attachmentRepository.findById(testAttachmentId)).thenReturn(Optional.of(testAttachment));

        // When
        Optional<AttachmentResponseDTO> result = attachmentService.findById(testAttachmentId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(testAttachmentId);
        assertThat(result.get().fileName()).isEqualTo(testAttachment.getFileName());
        verify(attachmentRepository).findById(testAttachmentId);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(attachmentRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When
        Optional<AttachmentResponseDTO> result = attachmentService.findById(nonExistentId);

        // Then
        assertThat(result).isEmpty();
        verify(attachmentRepository).findById(nonExistentId);
    }

    @Test
    void findAll_ShouldReturnPageOfAttachments() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Attachment> attachmentPage = new PageImpl<>(Arrays.asList(testAttachment));
        when(attachmentRepository.findAll(pageable)).thenReturn(attachmentPage);

        // When
        Page<AttachmentResponseDTO> result = attachmentService.findAll(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).id()).isEqualTo(testAttachmentId);
        verify(attachmentRepository).findAll(pageable);
    }

    @Test
    void update_ShouldUpdateAttachment_WhenValidData() {
        // Given
        AttachmentUpdateDTO dto = new AttachmentUpdateDTO(
            "updated.pdf",
            "https://example.com/updated.pdf",
            "amendment",
            "application/pdf",
            2048000L,
            "Updated description"
        );

        when(attachmentRepository.findById(testAttachmentId)).thenReturn(Optional.of(testAttachment));
        when(attachmentRepository.save(any(Attachment.class))).thenReturn(testAttachment);

        // When
        AttachmentResponseDTO result = attachmentService.update(testAttachmentId, dto);

        // Then
        assertThat(result).isNotNull();
        verify(attachmentRepository).findById(testAttachmentId);
        verify(attachmentRepository).save(any(Attachment.class));
    }

    @Test
    void update_ShouldThrowException_WhenAttachmentNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        AttachmentUpdateDTO dto = new AttachmentUpdateDTO(
            "updated.pdf",
            null,
            null,
            null,
            null,
            null
        );

        when(attachmentRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> attachmentService.update(nonExistentId, dto))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Attachment");

        verify(attachmentRepository).findById(nonExistentId);
        verify(attachmentRepository, never()).save(any(Attachment.class));
    }

    @Test
    void delete_ShouldDeleteAttachment_WhenExists() {
        // Given
        when(attachmentRepository.findById(testAttachmentId)).thenReturn(Optional.of(testAttachment));
        doNothing().when(attachmentRepository).delete(testAttachment);

        // When
        attachmentService.delete(testAttachmentId);

        // Then
        verify(attachmentRepository).findById(testAttachmentId);
        verify(attachmentRepository).delete(testAttachment);
    }

    @Test
    void delete_ShouldThrowException_WhenAttachmentNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(attachmentRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> attachmentService.delete(nonExistentId))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Attachment");

        verify(attachmentRepository).findById(nonExistentId);
        verify(attachmentRepository, never()).delete(any(Attachment.class));
    }

    @Test
    void findByOpportunityId_ShouldReturnListOfAttachments() {
        // Given
        List<Attachment> attachments = Arrays.asList(testAttachment);
        when(attachmentRepository.findByOpportunityId(testOpportunityId)).thenReturn(attachments);

        // When
        List<AttachmentResponseDTO> result = attachmentService.findByOpportunityId(testOpportunityId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).opportunityId()).isEqualTo(testOpportunityId);
        verify(attachmentRepository).findByOpportunityId(testOpportunityId);
    }

    @Test
    void findByType_ShouldReturnListOfAttachments() {
        // Given
        String type = "solicitation";
        List<Attachment> attachments = Arrays.asList(testAttachment);
        when(attachmentRepository.findByType(type)).thenReturn(attachments);

        // When
        List<AttachmentResponseDTO> result = attachmentService.findByType(type);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).type()).isEqualTo(type);
        verify(attachmentRepository).findByType(type);
    }

    @Test
    void findBySamAttachmentId_ShouldReturnAttachment_WhenExists() {
        // Given
        String samId = "SAM-ATT-001";
        when(attachmentRepository.findBySamAttachmentId(samId)).thenReturn(Optional.of(testAttachment));

        // When
        Optional<AttachmentResponseDTO> result = attachmentService.findBySamAttachmentId(samId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().samAttachmentId()).isEqualTo(samId);
        verify(attachmentRepository).findBySamAttachmentId(samId);
    }

    @Test
    void existsBySamAttachmentId_ShouldReturnTrue_WhenExists() {
        // Given
        String samId = "SAM-ATT-001";
        when(attachmentRepository.findBySamAttachmentId(samId)).thenReturn(Optional.of(testAttachment));

        // When
        boolean result = attachmentService.existsBySamAttachmentId(samId);

        // Then
        assertThat(result).isTrue();
        verify(attachmentRepository).findBySamAttachmentId(samId);
    }

    @Test
    void existsBySamAttachmentId_ShouldReturnFalse_WhenNotExists() {
        // Given
        String samId = "NON-EXISTENT";
        when(attachmentRepository.findBySamAttachmentId(samId)).thenReturn(Optional.empty());

        // When
        boolean result = attachmentService.existsBySamAttachmentId(samId);

        // Then
        assertThat(result).isFalse();
        verify(attachmentRepository).findBySamAttachmentId(samId);
    }
}
