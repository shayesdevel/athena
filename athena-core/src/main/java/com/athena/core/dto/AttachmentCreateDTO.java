package com.athena.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * DTO for creating a new attachment.
 */
public record AttachmentCreateDTO(
    @NotNull(message = "Opportunity ID is required")
    UUID opportunityId,

    @NotBlank(message = "File name is required")
    @Size(max = 500)
    String fileName,

    @NotBlank(message = "File URL is required")
    String fileUrl,

    @Size(max = 50)
    String type,

    @Size(max = 100)
    String mimeType,

    @Positive(message = "File size must be positive")
    Long fileSize,

    String description,

    @Size(max = 50)
    String samAttachmentId
) {
}
