package com.athena.core.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * DTO for updating an existing contact.
 */
public record ContactUpdateDTO(
    @Size(max = 100)
    String firstName,

    @Size(max = 100)
    String lastName,

    @Size(max = 255)
    String fullName,

    @Email(message = "Email must be valid")
    @Size(max = 255)
    String email,

    @Size(max = 20)
    String phone,

    @Size(max = 200)
    String title,

    UUID organizationId,

    UUID agencyId,

    UUID opportunityId,

    @Size(max = 50)
    String contactType,

    Boolean isPrimary
) {
}
