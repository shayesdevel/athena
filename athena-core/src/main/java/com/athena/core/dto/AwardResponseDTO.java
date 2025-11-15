package com.athena.core.dto;

import com.athena.core.entity.Award;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for award response.
 */
public record AwardResponseDTO(
    UUID id,
    UUID opportunityId,
    String contractNumber,
    String title,
    UUID organizationId,
    String awardeeName,
    String awardeeUei,
    String awardeeDuns,
    LocalDate awardDate,
    BigDecimal awardAmount,
    String currency,
    LocalDate startDate,
    LocalDate endDate,
    UUID agencyId,
    String awardingOffice,
    String awardType,
    String naicsCode,
    String setAside,
    String description,
    Boolean isActive,
    Instant createdAt,
    Instant updatedAt
) {
    /**
     * Create an AwardResponseDTO from an Award entity.
     */
    public static AwardResponseDTO fromEntity(Award award) {
        return new AwardResponseDTO(
            award.getId(),
            award.getOpportunity() != null ? award.getOpportunity().getId() : null,
            award.getContractNumber(),
            award.getTitle(),
            award.getOrganization() != null ? award.getOrganization().getId() : null,
            award.getAwardeeName(),
            award.getAwardeeUei(),
            award.getAwardeeDuns(),
            award.getAwardDate(),
            award.getAwardAmount(),
            award.getCurrency(),
            award.getStartDate(),
            award.getEndDate(),
            award.getAgency() != null ? award.getAgency().getId() : null,
            award.getAwardingOffice(),
            award.getAwardType(),
            award.getNaicsCode(),
            award.getSetAside(),
            award.getDescription(),
            award.getIsActive(),
            award.getCreatedAt(),
            award.getUpdatedAt()
        );
    }
}
