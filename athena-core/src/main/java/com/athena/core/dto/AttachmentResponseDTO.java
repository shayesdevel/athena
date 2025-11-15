package com.athena.core.dto;

import com.athena.core.entity.Attachment;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for attachment response.
 */
public record AttachmentResponseDTO(
    UUID id,
    UUID opportunityId,
    String fileName,
    String fileUrl,
    String type,
    String mimeType,
    Long fileSize,
    String description,
    String samAttachmentId,
    Instant createdAt,
    Instant updatedAt
) {
    /**
     * Create an AttachmentResponseDTO from an Attachment entity.
     */
    public static AttachmentResponseDTO fromEntity(Attachment attachment) {
        return new AttachmentResponseDTO(
            attachment.getId(),
            attachment.getOpportunity() != null ? attachment.getOpportunity().getId() : null,
            attachment.getFileName(),
            attachment.getFileUrl(),
            attachment.getType(),
            attachment.getMimeType(),
            attachment.getFileSize(),
            attachment.getDescription(),
            attachment.getSamAttachmentId(),
            attachment.getCreatedAt(),
            attachment.getUpdatedAt()
        );
    }
}
