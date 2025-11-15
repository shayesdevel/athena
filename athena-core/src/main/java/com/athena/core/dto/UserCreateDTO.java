package com.athena.core.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a new user.
 */
public record UserCreateDTO(
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255)
    String email,

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 100)
    String username,

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password,

    @Size(max = 100)
    String firstName,

    @Size(max = 100)
    String lastName,

    Boolean isAdmin
) {
}
