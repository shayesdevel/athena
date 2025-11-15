package com.athena.core.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * DTO for updating an existing attachment.
 */
public record AttachmentUpdateDTO(
    @Size(max = 500)
    String fileName,

    String fileUrl,

    @Size(max = 50)
    String type,

    @Size(max = 100)
    String mimeType,

    @Positive(message = "File size must be positive")
    Long fileSize,

    String description
) {
}
