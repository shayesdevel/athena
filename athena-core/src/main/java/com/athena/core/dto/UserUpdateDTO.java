package com.athena.core.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * DTO for updating an existing user.
 */
public record UserUpdateDTO(
    @Email(message = "Email must be valid")
    @Size(max = 255)
    String email,

    @Size(min = 3, max = 100)
    String username,

    @Size(max = 100)
    String firstName,

    @Size(max = 100)
    String lastName,

    Boolean isActive,

    Boolean isAdmin
) {
}
