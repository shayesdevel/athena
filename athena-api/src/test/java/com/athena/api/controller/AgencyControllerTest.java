package com.athena.api.controller;

import com.athena.api.AbstractControllerTest;
import com.athena.core.dto.AgencyCreateDTO;
import com.athena.core.dto.AgencyResponseDTO;
import com.athena.core.dto.AgencyUpdateDTO;
import com.athena.core.service.AgencyService;
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
 * Integration tests for AgencyController.
 * Tests all 10 endpoints with MockMvc.
 */
@WebMvcTest(
    controllers = AgencyController.class,
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
    }
)
class AgencyControllerTest extends AbstractControllerTest {

    @MockBean
    private AgencyService agencyService;

    @Test
    void findAll_ShouldReturnPageOfAgencies() throws Exception {
        // Given
        Instant now = Instant.now();
        AgencyResponseDTO agency1 = new AgencyResponseDTO(
                UUID.randomUUID(), "Department of Defense", "DOD",
                null, "Defense", "Tier1", true, now, now);
        AgencyResponseDTO agency2 = new AgencyResponseDTO(
                UUID.randomUUID(), "Department of State", "DOS",
                null, "State", "Tier1", true, now, now);
        Page<AgencyResponseDTO> page = new PageImpl<>(List.of(agency1, agency2), PageRequest.of(0, 10), 2);
        when(agencyService.findAll(any(Pageable.class))).thenReturn(page);

        // When/Then
        mockMvc.perform(get("/api/agencies"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Department of Defense"))
                .andExpect(jsonPath("$.content[1].name").value("Department of State"));
    }

    @Test
    void findById_WhenAgencyExists_ShouldReturnAgency() throws Exception {
        // Given
        UUID agencyId = UUID.randomUUID();
        Instant now = Instant.now();
        AgencyResponseDTO agency = new AgencyResponseDTO(
                agencyId, "Department of Defense", "DOD",
                null, "Defense", "Tier1", true, now, now);
        when(agencyService.findById(agencyId)).thenReturn(Optional.of(agency));

        // When/Then
        mockMvc.perform(get("/api/agencies/{id}", agencyId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(agencyId.toString()))
                .andExpect(jsonPath("$.name").value("Department of Defense"))
                .andExpect(jsonPath("$.abbreviation").value("DOD"));
    }

    @Test
    void findById_WhenAgencyNotFound_ShouldReturn404() throws Exception {
        // Given
        UUID agencyId = UUID.randomUUID();
        when(agencyService.findById(agencyId)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/api/agencies/{id}", agencyId))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_WithValidData_ShouldReturnCreatedAgency() throws Exception {
        // Given
        AgencyCreateDTO createDTO = new AgencyCreateDTO(
                "New Agency", "NA", null, "New Department", "Tier2", true);
        Instant now = Instant.now();
        AgencyResponseDTO created = new AgencyResponseDTO(
                UUID.randomUUID(), "New Agency", "NA",
                null, "New Department", "Tier2", true, now, now);
        when(agencyService.create(any(AgencyCreateDTO.class))).thenReturn(created);

        // When/Then
        mockMvc.perform(post("/api/agencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("New Agency"))
                .andExpect(jsonPath("$.abbreviation").value("NA"));
    }

    @Test
    void create_WithBlankName_ShouldReturn400() throws Exception {
        // Given - blank name
        AgencyCreateDTO invalidDTO = new AgencyCreateDTO(
                "", "NA", null, "New Department", "Tier2", true);

        // When/Then
        mockMvc.perform(post("/api/agencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_WithValidData_ShouldReturnUpdatedAgency() throws Exception {
        // Given
        UUID agencyId = UUID.randomUUID();
        AgencyUpdateDTO updateDTO = new AgencyUpdateDTO(
                "Updated Agency", "UA", null, "Updated Department", "Tier3", false);
        Instant now = Instant.now();
        AgencyResponseDTO updated = new AgencyResponseDTO(
                agencyId, "Updated Agency", "UA",
                null, "Updated Department", "Tier3", false, now, now);
        when(agencyService.update(eq(agencyId), any(AgencyUpdateDTO.class))).thenReturn(updated);

        // When/Then
        mockMvc.perform(put("/api/agencies/{id}", agencyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Updated Agency"));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        // Given
        UUID agencyId = UUID.randomUUID();

        // When/Then
        mockMvc.perform(delete("/api/agencies/{id}", agencyId))
                .andExpect(status().isNoContent());

        verify(agencyService).delete(agencyId);
    }

    @Test
    void findByAbbreviation_WhenAgencyExists_ShouldReturnAgency() throws Exception {
        // Given
        String abbreviation = "DOD";
        Instant now = Instant.now();
        AgencyResponseDTO agency = new AgencyResponseDTO(
                UUID.randomUUID(), "Department of Defense", abbreviation,
                null, "Defense", "Tier1", true, now, now);
        when(agencyService.findByAbbreviation(abbreviation)).thenReturn(Optional.of(agency));

        // When/Then
        mockMvc.perform(get("/api/agencies/abbreviation/{abbreviation}", abbreviation))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.abbreviation").value(abbreviation));
    }

    @Test
    void searchByName_ShouldReturnMatchingAgencies() throws Exception {
        // Given
        String searchName = "Defense";
        Instant now = Instant.now();
        AgencyResponseDTO agency = new AgencyResponseDTO(
                UUID.randomUUID(), "Department of Defense", "DOD",
                null, "Defense", "Tier1", true, now, now);
        when(agencyService.searchByName(searchName)).thenReturn(List.of(agency));

        // When/Then
        mockMvc.perform(get("/api/agencies/search")
                        .param("name", searchName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Department of Defense"));
    }

    @Test
    void findActiveAgencies_ShouldReturnActiveAgencies() throws Exception {
        // Given
        Instant now = Instant.now();
        AgencyResponseDTO agency = new AgencyResponseDTO(
                UUID.randomUUID(), "Active Agency", "AA",
                null, "Active", "Tier1", true, now, now);
        when(agencyService.findActiveAgencies()).thenReturn(List.of(agency));

        // When/Then
        mockMvc.perform(get("/api/agencies/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].isActive").value(true));
    }

    @Test
    void findSubAgencies_ShouldReturnSubAgencies() throws Exception {
        // Given
        UUID parentAgencyId = UUID.randomUUID();
        Instant now = Instant.now();
        AgencyResponseDTO subAgency = new AgencyResponseDTO(
                UUID.randomUUID(), "Sub Agency", "SA",
                parentAgencyId, "Defense", "Tier2", true, now, now);
        when(agencyService.findSubAgencies(parentAgencyId)).thenReturn(List.of(subAgency));

        // When/Then
        mockMvc.perform(get("/api/agencies/{parentAgencyId}/sub-agencies", parentAgencyId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].parentAgencyId").value(parentAgencyId.toString()));
    }

    @Test
    void findByDepartment_ShouldReturnMatchingAgencies() throws Exception {
        // Given
        String department = "Defense";
        Instant now = Instant.now();
        AgencyResponseDTO agency = new AgencyResponseDTO(
                UUID.randomUUID(), "Department of Defense", "DOD",
                null, department, "Tier1", true, now, now);
        when(agencyService.findByDepartment(department)).thenReturn(List.of(agency));

        // When/Then
        mockMvc.perform(get("/api/agencies/department/{department}", department))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].department").value(department));
    }
}
