package com.athena.core.dto;

import com.athena.core.entity.Opportunity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for opportunity response.
 */
public record OpportunityResponseDTO(
    UUID id,
    String noticeId,
    String title,
    String solicitationNumber,
    UUID agencyId,
    String officeName,
    String noticeType,
    String baseType,
    String archiveType,
    LocalDate archiveDate,
    String naicsCode,
    String classificationCode,
    String setAside,
    LocalDate postedDate,
    Instant responseDeadline,
    String description,
    String additionalInfoLink,
    String uiLink,
    String pointOfContact,
    String placeOfPerformanceCity,
    String placeOfPerformanceState,
    String placeOfPerformanceZip,
    String placeOfPerformanceCountry,
    Boolean isActive,
    Instant createdAt,
    Instant updatedAt
) {
    /**
     * Create an OpportunityResponseDTO from an Opportunity entity.
     */
    public static OpportunityResponseDTO fromEntity(Opportunity opp) {
        return new OpportunityResponseDTO(
            opp.getId(),
            opp.getNoticeId(),
            opp.getTitle(),
            opp.getSolicitationNumber(),
            opp.getAgency() != null ? opp.getAgency().getId() : null,
            opp.getOfficeName(),
            opp.getNoticeType(),
            opp.getBaseType(),
            opp.getArchiveType(),
            opp.getArchiveDate(),
            opp.getNaicsCode(),
            opp.getClassificationCode(),
            opp.getSetAside(),
            opp.getPostedDate(),
            opp.getResponseDeadline(),
            opp.getDescription(),
            opp.getAdditionalInfoLink(),
            opp.getUiLink(),
            opp.getPointOfContact(),
            opp.getPlaceOfPerformanceCity(),
            opp.getPlaceOfPerformanceState(),
            opp.getPlaceOfPerformanceZip(),
            opp.getPlaceOfPerformanceCountry(),
            opp.getIsActive(),
            opp.getCreatedAt(),
            opp.getUpdatedAt()
        );
    }
}
