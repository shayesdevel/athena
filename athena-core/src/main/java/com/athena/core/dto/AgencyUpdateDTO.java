package com.athena.core.dto;

import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * DTO for updating an existing agency.
 */
public record AgencyUpdateDTO(
    @Size(max = 500)
    String name,

    @Size(max = 50)
    String abbreviation,

    UUID parentAgencyId,

    @Size(max = 200)
    String department,

    @Size(max = 50)
    String tier,

    Boolean isActive
) {
}
