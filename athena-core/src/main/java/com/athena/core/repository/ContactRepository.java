package com.athena.core.repository;

import com.athena.core.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Contact entity operations.
 * Provides CRUD operations and custom queries for contact management.
 */
@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {

    /**
     * Find contact by email address.
     *
     * @param email the email to search for
     * @return Optional containing the contact if found
     */
    Optional<Contact> findByEmail(String email);

    /**
     * Find contacts by organization.
     *
     * @param organizationId the organization UUID
     * @return List of contacts for the organization
     */
    List<Contact> findByOrganizationId(UUID organizationId);

    /**
     * Find contacts by agency.
     *
     * @param agencyId the agency UUID
     * @return List of contacts for the agency
     */
    List<Contact> findByAgencyId(UUID agencyId);

    /**
     * Find contacts by opportunity.
     *
     * @param opportunityId the opportunity UUID
     * @return List of contacts for the opportunity
     */
    List<Contact> findByOpportunityId(UUID opportunityId);

    /**
     * Find primary contact for an organization.
     *
     * @param organizationId the organization UUID
     * @return Optional containing the primary contact if found
     */
    Optional<Contact> findByOrganizationIdAndIsPrimaryTrue(UUID organizationId);

    /**
     * Find primary contact for an agency.
     *
     * @param agencyId the agency UUID
     * @return Optional containing the primary contact if found
     */
    Optional<Contact> findByAgencyIdAndIsPrimaryTrue(UUID agencyId);

    /**
     * Find primary contact for an opportunity.
     *
     * @param opportunityId the opportunity UUID
     * @return Optional containing the primary contact if found
     */
    Optional<Contact> findByOpportunityIdAndIsPrimaryTrue(UUID opportunityId);

    /**
     * Find contacts by type.
     *
     * @param contactType the contact type to search for
     * @return List of contacts with matching type
     */
    List<Contact> findByContactType(String contactType);
}
