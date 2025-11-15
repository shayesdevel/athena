package com.athena.core.dto;

import com.athena.core.entity.NoticeType;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for notice type response.
 */
public record NoticeTypeResponseDTO(
    UUID id,
    String code,
    String name,
    String description,
    Boolean isActive,
    String category,
    Instant createdAt,
    Instant updatedAt
) {
    /**
     * Create a NoticeTypeResponseDTO from a NoticeType entity.
     */
    public static NoticeTypeResponseDTO fromEntity(NoticeType noticeType) {
        return new NoticeTypeResponseDTO(
            noticeType.getId(),
            noticeType.getCode(),
            noticeType.getName(),
            noticeType.getDescription(),
            noticeType.getIsActive(),
            noticeType.getCategory(),
            noticeType.getCreatedAt(),
            noticeType.getUpdatedAt()
        );
    }
}
