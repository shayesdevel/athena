package com.athena.api.controller;

import com.athena.api.AbstractControllerTest;
import com.athena.core.dto.ContactCreateDTO;
import com.athena.core.dto.ContactResponseDTO;
import com.athena.core.dto.ContactUpdateDTO;
import com.athena.core.service.ContactService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ContactController.
 * Tests all 13 endpoints with MockMvc.
 */
@WebMvcTest(
    controllers = ContactController.class,
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
    }
)
class ContactControllerTest extends AbstractControllerTest {

    @MockBean
    private ContactService contactService;

    @Test
    void findAll_ShouldReturnPageOfContacts() throws Exception {
        // Given
        Instant now = Instant.now();
        UUID orgId = UUID.randomUUID();
        ContactResponseDTO contact1 = new ContactResponseDTO(
                UUID.randomUUID(), "John", "Doe", "John Doe",
                "john.doe@example.com", "202-555-0100", "Contracting Officer",
                orgId, null, null, "Government", true, now, now);
        Page<ContactResponseDTO> page = new PageImpl<>(List.of(contact1), PageRequest.of(0, 10), 1);
        when(contactService.findAll(any(Pageable.class))).thenReturn(page);

        // When/Then
        mockMvc.perform(get("/api/contacts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].email").value("john.doe@example.com"));
    }

    @Test
    void findById_WhenContactExists_ShouldReturnContact() throws Exception {
        // Given
        UUID contactId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        Instant now = Instant.now();
        ContactResponseDTO contact = new ContactResponseDTO(
                contactId, "John", "Doe", "John Doe",
                "john.doe@example.com", "202-555-0100", "Contracting Officer",
                orgId, null, null, "Government", true, now, now);
        when(contactService.findById(contactId)).thenReturn(Optional.of(contact));

        // When/Then
        mockMvc.perform(get("/api/contacts/{id}", contactId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(contactId.toString()))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void findById_WhenContactNotFound_ShouldReturn404() throws Exception {
        // Given
        UUID contactId = UUID.randomUUID();
        when(contactService.findById(contactId)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/api/contacts/{id}", contactId))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_WithValidData_ShouldReturnCreatedContact() throws Exception {
        // Given
        UUID orgId = UUID.randomUUID();
        ContactCreateDTO createDTO = new ContactCreateDTO(
                "Jane", "Smith", "Jane Smith", "jane.smith@example.com",
                "703-555-0200", "Program Manager", orgId,
                null, null, "Contractor", true);
        Instant now = Instant.now();
        ContactResponseDTO created = new ContactResponseDTO(
                UUID.randomUUID(), "Jane", "Smith", "Jane Smith",
                "jane.smith@example.com", "703-555-0200", "Program Manager",
                orgId, null, null, "Contractor", true, now, now);
        when(contactService.create(any(ContactCreateDTO.class))).thenReturn(created);

        // When/Then
        mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("jane.smith@example.com"));
    }

    @Test
    void create_WithInvalidEmail_ShouldReturn400() throws Exception {
        // Given - invalid email
        UUID orgId = UUID.randomUUID();
        ContactCreateDTO invalidDTO = new ContactCreateDTO(
                "Jane", "Smith", "Jane Smith", "invalid-email",
                "703-555-0200", "Program Manager", orgId,
                null, null, "Contractor", true);

        // When/Then
        mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_WithValidData_ShouldReturnUpdatedContact() throws Exception {
        // Given
        UUID contactId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        ContactUpdateDTO updateDTO = new ContactUpdateDTO(
                "John", "Updated", "John Updated", "john.updated@example.com",
                "202-555-9999", "Senior Officer", orgId,
                null, null, "Government", false);
        Instant now = Instant.now();
        ContactResponseDTO updated = new ContactResponseDTO(
                contactId, "John", "Updated", "John Updated",
                "john.updated@example.com", "202-555-9999", "Senior Officer",
                orgId, null, null, "Government", false, now, now);
        when(contactService.update(eq(contactId), any(ContactUpdateDTO.class))).thenReturn(updated);

        // When/Then
        mockMvc.perform(put("/api/contacts/{id}", contactId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.lastName").value("Updated"));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        // Given
        UUID contactId = UUID.randomUUID();

        // When/Then
        mockMvc.perform(delete("/api/contacts/{id}", contactId))
                .andExpect(status().isNoContent());

        verify(contactService).delete(contactId);
    }

    @Test
    void findByEmail_WhenContactExists_ShouldReturnContact() throws Exception {
        // Given
        String email = "john.doe@example.com";
        UUID orgId = UUID.randomUUID();
        Instant now = Instant.now();
        ContactResponseDTO contact = new ContactResponseDTO(
                UUID.randomUUID(), "John", "Doe", "John Doe", email,
                "202-555-0100", "Contracting Officer", orgId,
                null, null, "Government", true, now, now);
        when(contactService.findByEmail(email)).thenReturn(Optional.of(contact));

        // When/Then
        mockMvc.perform(get("/api/contacts/email/{email}", email))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void findByOrganization_ShouldReturnContacts() throws Exception {
        // Given
        UUID orgId = UUID.randomUUID();
        Instant now = Instant.now();
        ContactResponseDTO contact = new ContactResponseDTO(
                UUID.randomUUID(), "John", "Doe", "John Doe",
                "john.doe@example.com", "202-555-0100", "Contracting Officer",
                orgId, null, null, "Contractor", true, now, now);
        when(contactService.findByOrganization(orgId)).thenReturn(List.of(contact));

        // When/Then
        mockMvc.perform(get("/api/contacts/organization/{organizationId}", orgId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].organizationId").value(orgId.toString()));
    }

    @Test
    void findByAgency_ShouldReturnContacts() throws Exception {
        // Given
        UUID agencyId = UUID.randomUUID();
        Instant now = Instant.now();
        ContactResponseDTO contact = new ContactResponseDTO(
                UUID.randomUUID(), "Jane", "Smith", "Jane Smith",
                "jane.smith@example.com", "703-555-0200", "Contracting Officer",
                null, agencyId, null, "Government", true, now, now);
        when(contactService.findByAgency(agencyId)).thenReturn(List.of(contact));

        // When/Then
        mockMvc.perform(get("/api/contacts/agency/{agencyId}", agencyId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].agencyId").value(agencyId.toString()));
    }

    @Test
    void findByOpportunity_ShouldReturnContacts() throws Exception {
        // Given
        UUID oppId = UUID.randomUUID();
        Instant now = Instant.now();
        ContactResponseDTO contact = new ContactResponseDTO(
                UUID.randomUUID(), "Bob", "Johnson", "Bob Johnson",
                "bob.johnson@example.com", "571-555-0300", "Point of Contact",
                null, null, oppId, "Government", true, now, now);
        when(contactService.findByOpportunity(oppId)).thenReturn(List.of(contact));

        // When/Then
        mockMvc.perform(get("/api/contacts/opportunity/{opportunityId}", oppId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].opportunityId").value(oppId.toString()));
    }

    @Test
    void findPrimaryContactForOrganization_WhenContactExists_ShouldReturnContact() throws Exception {
        // Given
        UUID orgId = UUID.randomUUID();
        Instant now = Instant.now();
        ContactResponseDTO contact = new ContactResponseDTO(
                UUID.randomUUID(), "Primary", "Contact", "Primary Contact",
                "primary@example.com", "202-555-0100", "Primary Officer",
                orgId, null, null, "Contractor", true, now, now);
        when(contactService.findPrimaryContactForOrganization(orgId)).thenReturn(Optional.of(contact));

        // When/Then
        mockMvc.perform(get("/api/contacts/organization/{organizationId}/primary", orgId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("primary@example.com"))
                .andExpect(jsonPath("$.isPrimary").value(true));
    }

    @Test
    void findPrimaryContactForAgency_WhenContactExists_ShouldReturnContact() throws Exception {
        // Given
        UUID agencyId = UUID.randomUUID();
        Instant now = Instant.now();
        ContactResponseDTO contact = new ContactResponseDTO(
                UUID.randomUUID(), "Agency", "Primary", "Agency Primary",
                "agency.primary@example.com", "703-555-0200", "Primary Officer",
                null, agencyId, null, "Government", true, now, now);
        when(contactService.findPrimaryContactForAgency(agencyId)).thenReturn(Optional.of(contact));

        // When/Then
        mockMvc.perform(get("/api/contacts/agency/{agencyId}/primary", agencyId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("agency.primary@example.com"))
                .andExpect(jsonPath("$.isPrimary").value(true));
    }

    @Test
    void findPrimaryContactForOpportunity_WhenContactExists_ShouldReturnContact() throws Exception {
        // Given
        UUID oppId = UUID.randomUUID();
        Instant now = Instant.now();
        ContactResponseDTO contact = new ContactResponseDTO(
                UUID.randomUUID(), "Opp", "Primary", "Opp Primary",
                "opp.primary@example.com", "571-555-0300", "Primary POC",
                null, null, oppId, "Government", true, now, now);
        when(contactService.findPrimaryContactForOpportunity(oppId)).thenReturn(Optional.of(contact));

        // When/Then
        mockMvc.perform(get("/api/contacts/opportunity/{opportunityId}/primary", oppId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("opp.primary@example.com"))
                .andExpect(jsonPath("$.isPrimary").value(true));
    }

    @Test
    void findByContactType_ShouldReturnContacts() throws Exception {
        // Given
        String contactType = "Government";
        UUID orgId = UUID.randomUUID();
        Instant now = Instant.now();
        ContactResponseDTO contact = new ContactResponseDTO(
                UUID.randomUUID(), "Type", "Contact", "Type Contact",
                "type.contact@example.com", "202-555-0100", "Contracting Officer",
                orgId, null, null, contactType, true, now, now);
        when(contactService.findByContactType(contactType)).thenReturn(List.of(contact));

        // When/Then
        mockMvc.perform(get("/api/contacts/type/{contactType}", contactType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].contactType").value(contactType));
    }
}
