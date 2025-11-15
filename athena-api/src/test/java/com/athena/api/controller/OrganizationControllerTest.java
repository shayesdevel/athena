package com.athena.api.controller;

import com.athena.api.AbstractControllerTest;
import com.athena.core.dto.OrganizationCreateDTO;
import com.athena.core.dto.OrganizationResponseDTO;
import com.athena.core.dto.OrganizationUpdateDTO;
import com.athena.core.service.OrganizationService;
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
 * Integration tests for OrganizationController.
 * Tests all 10 endpoints with MockMvc.
 */
@WebMvcTest(
    controllers = OrganizationController.class,
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
    }
)
class OrganizationControllerTest extends AbstractControllerTest {

    @MockBean
    private OrganizationService organizationService;

    @Test
    void findAll_ShouldReturnPageOfOrganizations() throws Exception {
        // Given
        Instant now = Instant.now();
        OrganizationResponseDTO org1 = new OrganizationResponseDTO(
                UUID.randomUUID(), "Acme Corp", "ABC123456789", "1A2B3",
                "123456789", "https://sam.gov/acme", "541511", "Corporation",
                true, false, false, false, "123 Main St", "Washington",
                "DC", "20001", "US", "https://acme.com", "202-555-0100",
                now, now);
        OrganizationResponseDTO org2 = new OrganizationResponseDTO(
                UUID.randomUUID(), "Beta Inc", "DEF987654321", "4C5D6",
                "987654321", "https://sam.gov/beta", "541512", "LLC",
                true, true, false, false, "456 Oak Ave", "Arlington",
                "VA", "22201", "US", "https://beta.com", "703-555-0200",
                now, now);
        Page<OrganizationResponseDTO> page = new PageImpl<>(List.of(org1, org2), PageRequest.of(0, 10), 2);
        when(organizationService.findAll(any(Pageable.class))).thenReturn(page);

        // When/Then
        mockMvc.perform(get("/api/organizations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Acme Corp"))
                .andExpect(jsonPath("$.content[1].name").value("Beta Inc"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void findById_WhenOrganizationExists_ShouldReturnOrganization() throws Exception {
        // Given
        UUID orgId = UUID.randomUUID();
        Instant now = Instant.now();
        OrganizationResponseDTO org = new OrganizationResponseDTO(
                orgId, "Acme Corp", "ABC123456789", "1A2B3",
                "123456789", "https://sam.gov/acme", "541511", "Corporation",
                true, false, false, false, "123 Main St", "Washington",
                "DC", "20001", "US", "https://acme.com", "202-555-0100",
                now, now);
        when(organizationService.findById(orgId)).thenReturn(Optional.of(org));

        // When/Then
        mockMvc.perform(get("/api/organizations/{id}", orgId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(orgId.toString()))
                .andExpect(jsonPath("$.name").value("Acme Corp"))
                .andExpect(jsonPath("$.uei").value("ABC123456789"));
    }

    @Test
    void findById_WhenOrganizationNotFound_ShouldReturn404() throws Exception {
        // Given
        UUID orgId = UUID.randomUUID();
        when(organizationService.findById(orgId)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/api/organizations/{id}", orgId))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_WithValidData_ShouldReturnCreatedOrganization() throws Exception {
        // Given
        OrganizationCreateDTO createDTO = new OrganizationCreateDTO(
                "New Org", "UEI123456789", "CAGE1", "111222333",
                "https://sam.gov/neworg", "541511", "Corporation",
                true, false, false, false, "123 Main St", "Washington",
                "DC", "20001", "US", "https://neworg.com", "202-555-0100");
        Instant now = Instant.now();
        OrganizationResponseDTO created = new OrganizationResponseDTO(
                UUID.randomUUID(), "New Org", "UEI123456789", "CAGE1",
                "111222333", "https://sam.gov/neworg", "541511", "Corporation",
                true, false, false, false, "123 Main St", "Washington",
                "DC", "20001", "US", "https://neworg.com", "202-555-0100",
                now, now);
        when(organizationService.create(any(OrganizationCreateDTO.class))).thenReturn(created);

        // When/Then
        mockMvc.perform(post("/api/organizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("New Org"))
                .andExpect(jsonPath("$.uei").value("UEI123456789"));
    }

    @Test
    void create_WithBlankName_ShouldReturn400() throws Exception {
        // Given - blank name
        OrganizationCreateDTO invalidDTO = new OrganizationCreateDTO(
                "", "UEI123456789", "CAGE1", "111222333",
                "https://sam.gov/neworg", "541511", "Corporation",
                true, false, false, false, "123 Main St", "Washington",
                "DC", "20001", "US", "https://neworg.com", "202-555-0100");

        // When/Then
        mockMvc.perform(post("/api/organizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_WithValidData_ShouldReturnUpdatedOrganization() throws Exception {
        // Given
        UUID orgId = UUID.randomUUID();
        OrganizationUpdateDTO updateDTO = new OrganizationUpdateDTO(
                "Updated Org", "UEI999999999", "CAGE9", "999888777",
                "https://sam.gov/updated", "541512", "LLC",
                false, true, false, false, "456 Oak Ave", "Arlington",
                "VA", "22201", "US", "https://updated.com", "703-555-0200");
        Instant now = Instant.now();
        OrganizationResponseDTO updated = new OrganizationResponseDTO(
                orgId, "Updated Org", "UEI999999999", "CAGE9",
                "999888777", "https://sam.gov/updated", "541512", "LLC",
                false, true, false, false, "456 Oak Ave", "Arlington",
                "VA", "22201", "US", "https://updated.com", "703-555-0200",
                now, now);
        when(organizationService.update(eq(orgId), any(OrganizationUpdateDTO.class))).thenReturn(updated);

        // When/Then
        mockMvc.perform(put("/api/organizations/{id}", orgId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Updated Org"));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        // Given
        UUID orgId = UUID.randomUUID();

        // When/Then
        mockMvc.perform(delete("/api/organizations/{id}", orgId))
                .andExpect(status().isNoContent());

        verify(organizationService).delete(orgId);
    }

    @Test
    void findByUei_WhenOrganizationExists_ShouldReturnOrganization() throws Exception {
        // Given
        String uei = "ABC123456789";
        Instant now = Instant.now();
        OrganizationResponseDTO org = new OrganizationResponseDTO(
                UUID.randomUUID(), "Acme Corp", uei, "1A2B3",
                "123456789", "https://sam.gov/acme", "541511", "Corporation",
                true, false, false, false, "123 Main St", "Washington",
                "DC", "20001", "US", "https://acme.com", "202-555-0100",
                now, now);
        when(organizationService.findByUei(uei)).thenReturn(Optional.of(org));

        // When/Then
        mockMvc.perform(get("/api/organizations/uei/{uei}", uei))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.uei").value(uei));
    }

    @Test
    void findByUei_WhenOrganizationNotFound_ShouldReturn404() throws Exception {
        // Given
        String uei = "NOTFOUND12345";
        when(organizationService.findByUei(uei)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/api/organizations/uei/{uei}", uei))
                .andExpect(status().isNotFound());
    }

    @Test
    void findByCageCode_WhenOrganizationExists_ShouldReturnOrganization() throws Exception {
        // Given
        String cageCode = "1A2B3";
        Instant now = Instant.now();
        OrganizationResponseDTO org = new OrganizationResponseDTO(
                UUID.randomUUID(), "Acme Corp", "ABC123456789", cageCode,
                "123456789", "https://sam.gov/acme", "541511", "Corporation",
                true, false, false, false, "123 Main St", "Washington",
                "DC", "20001", "US", "https://acme.com", "202-555-0100",
                now, now);
        when(organizationService.findByCageCode(cageCode)).thenReturn(Optional.of(org));

        // When/Then
        mockMvc.perform(get("/api/organizations/cage/{cageCode}", cageCode))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cageCode").value(cageCode));
    }

    @Test
    void searchByName_ShouldReturnMatchingOrganizations() throws Exception {
        // Given
        String searchName = "Acme";
        Instant now = Instant.now();
        OrganizationResponseDTO org = new OrganizationResponseDTO(
                UUID.randomUUID(), "Acme Corp", "ABC123456789", "1A2B3",
                "123456789", "https://sam.gov/acme", "541511", "Corporation",
                true, false, false, false, "123 Main St", "Washington",
                "DC", "20001", "US", "https://acme.com", "202-555-0100",
                now, now);
        when(organizationService.searchByName(searchName)).thenReturn(List.of(org));

        // When/Then
        mockMvc.perform(get("/api/organizations/search")
                        .param("name", searchName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Acme Corp"));
    }

    @Test
    void findByPrimaryNaics_ShouldReturnMatchingOrganizations() throws Exception {
        // Given
        String naics = "541511";
        Instant now = Instant.now();
        OrganizationResponseDTO org = new OrganizationResponseDTO(
                UUID.randomUUID(), "Acme Corp", "ABC123456789", "1A2B3",
                "123456789", "https://sam.gov/acme", naics, "Corporation",
                true, false, false, false, "123 Main St", "Washington",
                "DC", "20001", "US", "https://acme.com", "202-555-0100",
                now, now);
        when(organizationService.findByPrimaryNaics(naics)).thenReturn(List.of(org));

        // When/Then
        mockMvc.perform(get("/api/organizations/naics/{primaryNaics}", naics))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].primaryNaics").value(naics));
    }

    @Test
    void findSmallBusinesses_ShouldReturnSmallBusinessOrganizations() throws Exception {
        // Given
        Instant now = Instant.now();
        OrganizationResponseDTO org = new OrganizationResponseDTO(
                UUID.randomUUID(), "Small Biz Inc", "SB123456789", "SB123",
                "111222333", "https://sam.gov/smallbiz", "541511", "Corporation",
                true, false, false, false, "123 Main St", "Washington",
                "DC", "20001", "US", "https://smallbiz.com", "202-555-0100",
                now, now);
        when(organizationService.findSmallBusinesses()).thenReturn(List.of(org));

        // When/Then
        mockMvc.perform(get("/api/organizations/small-business"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].isSmallBusiness").value(true));
    }
}
