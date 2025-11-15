package com.athena.core.dto;

import com.athena.core.entity.Contact;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for contact response.
 */
public record ContactResponseDTO(
    UUID id,
    String firstName,
    String lastName,
    String fullName,
    String email,
    String phone,
    String title,
    UUID organizationId,
    UUID agencyId,
    UUID opportunityId,
    String contactType,
    Boolean isPrimary,
    Instant createdAt,
    Instant updatedAt
) {
    /**
     * Create a ContactResponseDTO from a Contact entity.
     */
    public static ContactResponseDTO fromEntity(Contact contact) {
        return new ContactResponseDTO(
            contact.getId(),
            contact.getFirstName(),
            contact.getLastName(),
            contact.getFullName(),
            contact.getEmail(),
            contact.getPhone(),
            contact.getTitle(),
            contact.getOrganization() != null ? contact.getOrganization().getId() : null,
            contact.getAgency() != null ? contact.getAgency().getId() : null,
            contact.getOpportunity() != null ? contact.getOpportunity().getId() : null,
            contact.getContactType(),
            contact.getIsPrimary(),
            contact.getCreatedAt(),
            contact.getUpdatedAt()
        );
    }
}
