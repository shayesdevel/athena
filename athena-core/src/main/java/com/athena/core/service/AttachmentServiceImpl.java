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
import com.athena.core.service.AttachmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of AttachmentService.
 */
@Service
@Transactional(readOnly = true)
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final OpportunityRepository opportunityRepository;

    public AttachmentServiceImpl(AttachmentRepository attachmentRepository,
                                  OpportunityRepository opportunityRepository) {
        this.attachmentRepository = attachmentRepository;
        this.opportunityRepository = opportunityRepository;
    }

    @Override
    @Transactional
    public AttachmentResponseDTO create(AttachmentCreateDTO dto) {
        // Verify opportunity exists
        Opportunity opportunity = opportunityRepository.findById(dto.opportunityId())
            .orElseThrow(() -> new EntityNotFoundException("Opportunity", dto.opportunityId()));

        // Check for duplicate SAM attachment ID if provided
        if (dto.samAttachmentId() != null &&
            attachmentRepository.findBySamAttachmentId(dto.samAttachmentId()).isPresent()) {
            throw new DuplicateEntityException("Attachment", "samAttachmentId", dto.samAttachmentId());
        }

        // Create attachment entity
        Attachment attachment = new Attachment(opportunity, dto.fileName(), dto.fileUrl());
        attachment.setType(dto.type());
        attachment.setMimeType(dto.mimeType());
        attachment.setFileSize(dto.fileSize());
        attachment.setDescription(dto.description());
        attachment.setSamAttachmentId(dto.samAttachmentId());

        // Save and return
        Attachment savedAttachment = attachmentRepository.save(attachment);
        return AttachmentResponseDTO.fromEntity(savedAttachment);
    }

    @Override
    public Optional<AttachmentResponseDTO> findById(UUID id) {
        return attachmentRepository.findById(id)
            .map(AttachmentResponseDTO::fromEntity);
    }

    @Override
    public Page<AttachmentResponseDTO> findAll(Pageable pageable) {
        return attachmentRepository.findAll(pageable)
            .map(AttachmentResponseDTO::fromEntity);
    }

    @Override
    @Transactional
    public AttachmentResponseDTO update(UUID id, AttachmentUpdateDTO dto) {
        Attachment attachment = attachmentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Attachment", id));

        // Update fields if provided
        if (dto.fileName() != null) {
            attachment.setFileName(dto.fileName());
        }

        if (dto.fileUrl() != null) {
            attachment.setFileUrl(dto.fileUrl());
        }

        if (dto.type() != null) {
            attachment.setType(dto.type());
        }

        if (dto.mimeType() != null) {
            attachment.setMimeType(dto.mimeType());
        }

        if (dto.fileSize() != null) {
            attachment.setFileSize(dto.fileSize());
        }

        if (dto.description() != null) {
            attachment.setDescription(dto.description());
        }

        Attachment updatedAttachment = attachmentRepository.save(attachment);
        return AttachmentResponseDTO.fromEntity(updatedAttachment);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Attachment attachment = attachmentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Attachment", id));

        // Hard delete for attachments
        attachmentRepository.delete(attachment);
    }

    @Override
    public List<AttachmentResponseDTO> findByOpportunityId(UUID opportunityId) {
        return attachmentRepository.findByOpportunityId(opportunityId)
            .stream()
            .map(AttachmentResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<AttachmentResponseDTO> findByType(String type) {
        return attachmentRepository.findByType(type)
            .stream()
            .map(AttachmentResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<AttachmentResponseDTO> findBySamAttachmentId(String samAttachmentId) {
        return attachmentRepository.findBySamAttachmentId(samAttachmentId)
            .map(AttachmentResponseDTO::fromEntity);
    }

    @Override
    public boolean existsBySamAttachmentId(String samAttachmentId) {
        return attachmentRepository.findBySamAttachmentId(samAttachmentId).isPresent();
    }
}
