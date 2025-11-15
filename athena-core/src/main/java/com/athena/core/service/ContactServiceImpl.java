package com.athena.core.service;

import com.athena.core.dto.ContactCreateDTO;
import com.athena.core.dto.ContactResponseDTO;
import com.athena.core.dto.ContactUpdateDTO;
import com.athena.core.entity.Agency;
import com.athena.core.entity.Contact;
import com.athena.core.entity.Opportunity;
import com.athena.core.entity.Organization;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.AgencyRepository;
import com.athena.core.repository.ContactRepository;
import com.athena.core.repository.OpportunityRepository;
import com.athena.core.repository.OrganizationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of ContactService.
 */
@Service
@Transactional(readOnly = true)
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final OrganizationRepository organizationRepository;
    private final AgencyRepository agencyRepository;
    private final OpportunityRepository opportunityRepository;

    public ContactServiceImpl(ContactRepository contactRepository,
                              OrganizationRepository organizationRepository,
                              AgencyRepository agencyRepository,
                              OpportunityRepository opportunityRepository) {
        this.contactRepository = contactRepository;
        this.organizationRepository = organizationRepository;
        this.agencyRepository = agencyRepository;
        this.opportunityRepository = opportunityRepository;
    }

    @Override
    @Transactional
    public ContactResponseDTO create(ContactCreateDTO dto) {
        Contact contact = new Contact();

        // Set basic fields
        contact.setFirstName(dto.firstName());
        contact.setLastName(dto.lastName());
        contact.setFullName(dto.fullName());
        contact.setEmail(dto.email());
        contact.setPhone(dto.phone());
        contact.setTitle(dto.title());
        contact.setContactType(dto.contactType());
        contact.setIsPrimary(dto.isPrimary() != null ? dto.isPrimary() : false);

        // Set relationships
        setContactRelationships(contact, dto.organizationId(), dto.agencyId(), dto.opportunityId());

        Contact savedContact = contactRepository.save(contact);
        return ContactResponseDTO.fromEntity(savedContact);
    }

    @Override
    public Optional<ContactResponseDTO> findById(UUID id) {
        return contactRepository.findById(id)
            .map(ContactResponseDTO::fromEntity);
    }

    @Override
    public Page<ContactResponseDTO> findAll(Pageable pageable) {
        return contactRepository.findAll(pageable)
            .map(ContactResponseDTO::fromEntity);
    }

    @Override
    @Transactional
    public ContactResponseDTO update(UUID id, ContactUpdateDTO dto) {
        Contact contact = contactRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Contact", id));

        // Update fields if provided
        if (dto.firstName() != null) contact.setFirstName(dto.firstName());
        if (dto.lastName() != null) contact.setLastName(dto.lastName());
        if (dto.fullName() != null) contact.setFullName(dto.fullName());
        if (dto.email() != null) contact.setEmail(dto.email());
        if (dto.phone() != null) contact.setPhone(dto.phone());
        if (dto.title() != null) contact.setTitle(dto.title());
        if (dto.contactType() != null) contact.setContactType(dto.contactType());
        if (dto.isPrimary() != null) contact.setIsPrimary(dto.isPrimary());

        // Update relationships if any are provided
        if (dto.organizationId() != null || dto.agencyId() != null || dto.opportunityId() != null) {
            setContactRelationships(contact, dto.organizationId(), dto.agencyId(), dto.opportunityId());
        }

        Contact updatedContact = contactRepository.save(contact);
        return ContactResponseDTO.fromEntity(updatedContact);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!contactRepository.existsById(id)) {
            throw new EntityNotFoundException("Contact", id);
        }
        contactRepository.deleteById(id);
    }

    @Override
    public Optional<ContactResponseDTO> findByEmail(String email) {
        return contactRepository.findByEmail(email)
            .map(ContactResponseDTO::fromEntity);
    }

    @Override
    public List<ContactResponseDTO> findByOrganization(UUID organizationId) {
        return contactRepository.findByOrganizationId(organizationId)
            .stream()
            .map(ContactResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<ContactResponseDTO> findByAgency(UUID agencyId) {
        return contactRepository.findByAgencyId(agencyId)
            .stream()
            .map(ContactResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<ContactResponseDTO> findByOpportunity(UUID opportunityId) {
        return contactRepository.findByOpportunityId(opportunityId)
            .stream()
            .map(ContactResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<ContactResponseDTO> findPrimaryContactForOrganization(UUID organizationId) {
        return contactRepository.findByOrganizationIdAndIsPrimaryTrue(organizationId)
            .map(ContactResponseDTO::fromEntity);
    }

    @Override
    public Optional<ContactResponseDTO> findPrimaryContactForAgency(UUID agencyId) {
        return contactRepository.findByAgencyIdAndIsPrimaryTrue(agencyId)
            .map(ContactResponseDTO::fromEntity);
    }

    @Override
    public Optional<ContactResponseDTO> findPrimaryContactForOpportunity(UUID opportunityId) {
        return contactRepository.findByOpportunityIdAndIsPrimaryTrue(opportunityId)
            .map(ContactResponseDTO::fromEntity);
    }

    @Override
    public List<ContactResponseDTO> findByContactType(String contactType) {
        return contactRepository.findByContactType(contactType)
            .stream()
            .map(ContactResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Set contact relationships (organization, agency, or opportunity).
     */
    private void setContactRelationships(Contact contact, UUID organizationId, UUID agencyId, UUID opportunityId) {
        if (organizationId != null) {
            Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Organization", organizationId));
            contact.setOrganization(organization);
        }

        if (agencyId != null) {
            Agency agency = agencyRepository.findById(agencyId)
                .orElseThrow(() -> new EntityNotFoundException("Agency", agencyId));
            contact.setAgency(agency);
        }

        if (opportunityId != null) {
            Opportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new EntityNotFoundException("Opportunity", opportunityId));
            contact.setOpportunity(opportunity);
        }
    }
}
