package com.athena.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a new organization.
 */
public record OrganizationCreateDTO(
    @NotBlank(message = "Organization name is required")
    @Size(max = 500)
    String name,

    @Size(max = 12)
    String uei,

    @Size(max = 5)
    String cageCode,

    @Size(max = 9)
    String duns,

    String samUrl,

    @Size(max = 6)
    String primaryNaics,

    @Size(max = 100)
    String businessType,

    Boolean isSmallBusiness,

    Boolean isWomanOwned,

    Boolean isVeteranOwned,

    Boolean is8aCertified,

    @Size(max = 500)
    String streetAddress,

    @Size(max = 100)
    String city,

    @Size(max = 2)
    String stateCode,

    @Size(max = 10)
    String zipCode,

    @Size(max = 2)
    String countryCode,

    String websiteUrl,

    @Size(max = 20)
    String phone
) {
}
