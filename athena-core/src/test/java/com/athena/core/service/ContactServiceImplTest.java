package com.athena.core.service;

import com.athena.core.dto.ContactCreateDTO;
import com.athena.core.dto.ContactResponseDTO;
import com.athena.core.entity.Contact;
import com.athena.core.entity.Organization;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.AgencyRepository;
import com.athena.core.repository.ContactRepository;
import com.athena.core.repository.OpportunityRepository;
import com.athena.core.repository.OrganizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceImplTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private AgencyRepository agencyRepository;

    @Mock
    private OpportunityRepository opportunityRepository;

    @InjectMocks
    private ContactServiceImpl contactService;

    private Contact testContact;
    private UUID testContactId;

    @BeforeEach
    void setUp() {
        testContactId = UUID.randomUUID();
        testContact = new Contact("John", "Doe", "john.doe@example.com");
        testContact.setId(testContactId);
        testContact.setPhone("555-1234");
        testContact.setTitle("Project Manager");
        testContact.setIsPrimary(true);
    }

    @Test
    void create_ShouldCreateContact_WhenValidData() {
        // Given
        ContactCreateDTO dto = new ContactCreateDTO(
            "Jane",
            "Smith",
            null,
            "jane.smith@example.com",
            "555-5678",
            "Director",
            null,
            null,
            null,
            "Primary",
            false
        );

        when(contactRepository.save(any(Contact.class))).thenReturn(testContact);

        // When
        ContactResponseDTO result = contactService.create(dto);

        // Then
        assertThat(result).isNotNull();
        verify(contactRepository).save(any(Contact.class));
    }

    @Test
    void create_ShouldSetOrganization_WhenProvided() {
        // Given
        UUID orgId = UUID.randomUUID();
        Organization org = new Organization("Test Org");
        ContactCreateDTO dto = new ContactCreateDTO(
            "Jane",
            "Smith",
            null,
            "jane.smith@example.com",
            null,
            null,
            orgId,
            null,
            null,
            null,
            false
        );

        when(organizationRepository.findById(orgId)).thenReturn(Optional.of(org));
        when(contactRepository.save(any(Contact.class))).thenReturn(testContact);

        // When
        ContactResponseDTO result = contactService.create(dto);

        // Then
        assertThat(result).isNotNull();
        verify(organizationRepository).findById(orgId);
        verify(contactRepository).save(any(Contact.class));
    }

    @Test
    void findById_ShouldReturnContact_WhenExists() {
        // Given
        when(contactRepository.findById(testContactId)).thenReturn(Optional.of(testContact));

        // When
        Optional<ContactResponseDTO> result = contactService.findById(testContactId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(testContactId);
        verify(contactRepository).findById(testContactId);
    }

    @Test
    void findByEmail_ShouldReturnContact_WhenExists() {
        // Given
        when(contactRepository.findByEmail("john.doe@example.com"))
            .thenReturn(Optional.of(testContact));

        // When
        Optional<ContactResponseDTO> result = contactService.findByEmail("john.doe@example.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().email()).isEqualTo("john.doe@example.com");
        verify(contactRepository).findByEmail("john.doe@example.com");
    }

    @Test
    void findByOrganization_ShouldReturnOrganizationContacts() {
        // Given
        UUID orgId = UUID.randomUUID();
        when(contactRepository.findByOrganizationId(orgId))
            .thenReturn(Arrays.asList(testContact));

        // When
        List<ContactResponseDTO> result = contactService.findByOrganization(orgId);

        // Then
        assertThat(result).hasSize(1);
        verify(contactRepository).findByOrganizationId(orgId);
    }

    @Test
    void findPrimaryContactForOrganization_ShouldReturnPrimaryContact() {
        // Given
        UUID orgId = UUID.randomUUID();
        when(contactRepository.findByOrganizationIdAndIsPrimaryTrue(orgId))
            .thenReturn(Optional.of(testContact));

        // When
        Optional<ContactResponseDTO> result = contactService.findPrimaryContactForOrganization(orgId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().isPrimary()).isTrue();
        verify(contactRepository).findByOrganizationIdAndIsPrimaryTrue(orgId);
    }

    @Test
    void delete_ShouldDeleteContact_WhenExists() {
        // Given
        when(contactRepository.existsById(testContactId)).thenReturn(true);

        // When
        contactService.delete(testContactId);

        // Then
        verify(contactRepository).existsById(testContactId);
        verify(contactRepository).deleteById(testContactId);
    }

    @Test
    void delete_ShouldThrowException_WhenNotExists() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(contactRepository.existsById(nonExistentId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> contactService.delete(nonExistentId))
            .isInstanceOf(EntityNotFoundException.class);

        verify(contactRepository).existsById(nonExistentId);
        verify(contactRepository, never()).deleteById(any());
    }
}
