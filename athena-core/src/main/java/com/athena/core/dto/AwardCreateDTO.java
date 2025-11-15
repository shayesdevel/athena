package com.athena.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for creating a new award.
 */
public record AwardCreateDTO(
    UUID opportunityId,

    @NotBlank(message = "Contract number is required")
    @Size(max = 255)
    String contractNumber,

    @Size(max = 500)
    String title,

    UUID organizationId,

    @Size(max = 500)
    String awardeeName,

    @Size(max = 12)
    String awardeeUei,

    @Size(max = 9)
    String awardeeDuns,

    LocalDate awardDate,

    @Positive(message = "Award amount must be positive")
    BigDecimal awardAmount,

    @Size(max = 3)
    String currency,

    LocalDate startDate,

    LocalDate endDate,

    UUID agencyId,

    @Size(max = 500)
    String awardingOffice,

    @Size(max = 100)
    String awardType,

    @Size(max = 6)
    String naicsCode,

    @Size(max = 100)
    String setAside,

    String description
) {
}
