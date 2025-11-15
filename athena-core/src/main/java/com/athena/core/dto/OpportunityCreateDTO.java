package com.athena.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for creating a new opportunity.
 */
public record OpportunityCreateDTO(
    @NotBlank(message = "Notice ID is required")
    @Size(max = 255)
    String noticeId,

    @NotBlank(message = "Title is required")
    String title,

    @Size(max = 255)
    String solicitationNumber,

    UUID agencyId,

    @Size(max = 500)
    String officeName,

    @NotBlank(message = "Notice type is required")
    @Size(max = 50)
    String noticeType,

    @Size(max = 50)
    String baseType,

    @Size(max = 50)
    String archiveType,

    LocalDate archiveDate,

    @Size(max = 6)
    String naicsCode,

    @Size(max = 10)
    String classificationCode,

    @Size(max = 100)
    String setAside,

    LocalDate postedDate,

    Instant responseDeadline,

    String description,

    String additionalInfoLink,

    String uiLink,

    @Size(max = 255)
    String pointOfContact,

    @Size(max = 100)
    String placeOfPerformanceCity,

    @Size(max = 2)
    String placeOfPerformanceState,

    @Size(max = 10)
    String placeOfPerformanceZip,

    @Size(max = 2)
    String placeOfPerformanceCountry,

    Boolean isActive
) {
}
