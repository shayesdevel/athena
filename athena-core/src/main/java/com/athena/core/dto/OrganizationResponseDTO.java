package com.athena.core.dto;

import com.athena.core.entity.Organization;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for organization response.
 */
public record OrganizationResponseDTO(
    UUID id,
    String name,
    String uei,
    String cageCode,
    String duns,
    String samUrl,
    String primaryNaics,
    String businessType,
    Boolean isSmallBusiness,
    Boolean isWomanOwned,
    Boolean isVeteranOwned,
    Boolean is8aCertified,
    String streetAddress,
    String city,
    String stateCode,
    String zipCode,
    String countryCode,
    String websiteUrl,
    String phone,
    Instant createdAt,
    Instant updatedAt
) {
    /**
     * Create an OrganizationResponseDTO from an Organization entity.
     */
    public static OrganizationResponseDTO fromEntity(Organization org) {
        return new OrganizationResponseDTO(
            org.getId(),
            org.getName(),
            org.getUei(),
            org.getCageCode(),
            org.getDuns(),
            org.getSamUrl(),
            org.getPrimaryNaics(),
            org.getBusinessType(),
            org.getIsSmallBusiness(),
            org.getIsWomanOwned(),
            org.getIsVeteranOwned(),
            org.getIs8aCertified(),
            org.getStreetAddress(),
            org.getCity(),
            org.getStateCode(),
            org.getZipCode(),
            org.getCountryCode(),
            org.getWebsiteUrl(),
            org.getPhone(),
            org.getCreatedAt(),
            org.getUpdatedAt()
        );
    }
}
