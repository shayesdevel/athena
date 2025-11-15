package com.athena.core.dto;

import com.athena.core.entity.User;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for user response (excludes sensitive data like password hash).
 */
public record UserResponseDTO(
    UUID id,
    String email,
    String username,
    String firstName,
    String lastName,
    Boolean isActive,
    Boolean isAdmin,
    Instant lastLoginAt,
    Instant createdAt,
    Instant updatedAt
) {
    /**
     * Create a UserResponseDTO from a User entity.
     */
    public static UserResponseDTO fromEntity(User user) {
        return new UserResponseDTO(
            user.getId(),
            user.getEmail(),
            user.getUsername(),
            user.getFirstName(),
            user.getLastName(),
            user.getIsActive(),
            user.getIsAdmin(),
            user.getLastLoginAt(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
