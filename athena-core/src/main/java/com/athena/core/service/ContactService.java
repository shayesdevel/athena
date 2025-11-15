package com.athena.core.service;

import com.athena.core.dto.ContactCreateDTO;
import com.athena.core.dto.ContactResponseDTO;
import com.athena.core.dto.ContactUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for Contact entity operations.
 * Manages points of contact for organizations, agencies, and opportunities.
 */
public interface ContactService {

    /**
     * Create a new contact.
     *
     * @param dto the contact creation data
     * @return the created contact
     */
    ContactResponseDTO create(ContactCreateDTO dto);

    /**
     * Find contact by ID.
     *
     * @param id the contact UUID
     * @return Optional containing the contact if found
     */
    Optional<ContactResponseDTO> findById(UUID id);

    /**
     * Find all contacts with pagination.
     *
     * @param pageable pagination parameters
     * @return page of contacts
     */
    Page<ContactResponseDTO> findAll(Pageable pageable);

    /**
     * Update an existing contact.
     *
     * @param id the contact UUID
     * @param dto the update data
     * @return the updated contact
     * @throws com.athena.core.exception.EntityNotFoundException if contact not found
     */
    ContactResponseDTO update(UUID id, ContactUpdateDTO dto);

    /**
     * Delete a contact.
     *
     * @param id the contact UUID
     * @throws com.athena.core.exception.EntityNotFoundException if contact not found
     */
    void delete(UUID id);

    /**
     * Find contact by email address.
     *
     * @param email the email to search for
     * @return Optional containing the contact if found
     */
    Optional<ContactResponseDTO> findByEmail(String email);

    /**
     * Find contacts by organization.
     *
     * @param organizationId the organization UUID
     * @return List of contacts for the organization
     */
    List<ContactResponseDTO> findByOrganization(UUID organizationId);

    /**
     * Find contacts by agency.
     *
     * @param agencyId the agency UUID
     * @return List of contacts for the agency
     */
    List<ContactResponseDTO> findByAgency(UUID agencyId);

    /**
     * Find contacts by opportunity.
     *
     * @param opportunityId the opportunity UUID
     * @return List of contacts for the opportunity
     */
    List<ContactResponseDTO> findByOpportunity(UUID opportunityId);

    /**
     * Find primary contact for an organization.
     *
     * @param organizationId the organization UUID
     * @return Optional containing the primary contact if found
     */
    Optional<ContactResponseDTO> findPrimaryContactForOrganization(UUID organizationId);

    /**
     * Find primary contact for an agency.
     *
     * @param agencyId the agency UUID
     * @return Optional containing the primary contact if found
     */
    Optional<ContactResponseDTO> findPrimaryContactForAgency(UUID agencyId);

    /**
     * Find primary contact for an opportunity.
     *
     * @param opportunityId the opportunity UUID
     * @return Optional containing the primary contact if found
     */
    Optional<ContactResponseDTO> findPrimaryContactForOpportunity(UUID opportunityId);

    /**
     * Find contacts by type.
     *
     * @param contactType the contact type to search for
     * @return List of contacts with matching type
     */
    List<ContactResponseDTO> findByContactType(String contactType);
}
