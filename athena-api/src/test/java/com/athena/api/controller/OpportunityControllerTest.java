package com.athena.api.controller;

import com.athena.api.AbstractControllerTest;
import com.athena.core.dto.OpportunityCreateDTO;
import com.athena.core.dto.OpportunityResponseDTO;
import com.athena.core.dto.OpportunityUpdateDTO;
import com.athena.core.service.OpportunityService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import java.time.Instant;
import java.time.LocalDate;
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
 * Integration tests for OpportunityController.
 * Tests all 14 endpoints with MockMvc.
 */
@WebMvcTest(
    controllers = OpportunityController.class,
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
    }
)
class OpportunityControllerTest extends AbstractControllerTest {

    @MockBean
    private OpportunityService opportunityService;

    @Test
    void findAll_ShouldReturnPageOfOpportunities() throws Exception {
        // Given
        Instant now = Instant.now();
        LocalDate today = LocalDate.now();
        UUID agencyId = UUID.randomUUID();

        OpportunityResponseDTO opp1 = new OpportunityResponseDTO(
                UUID.randomUUID(), "NOTICE-001", "Software Development",
                "SOL-2025-001", agencyId, "Office of IT",
                "Presolicitation", "Combined Synopsis/Solicitation", null, null,
                "541511", "R", "8(a)", today, now.plusSeconds(86400 * 30),
                "Description of opportunity", "https://sam.gov/additional",
                "https://sam.gov/opp/001", "John Doe, john@agency.gov",
                "Washington", "DC", "20500", "US",
                true, now, now);

        OpportunityResponseDTO opp2 = new OpportunityResponseDTO(
                UUID.randomUUID(), "NOTICE-002", "Consulting Services",
                "SOL-2025-002", agencyId, "Office of Consulting",
                "Solicitation", "Request for Proposal", null, null,
                "541611", "R", "SDVOSB", today, now.plusSeconds(86400 * 45),
                "Description of consulting opportunity", "https://sam.gov/additional2",
                "https://sam.gov/opp/002", "Jane Smith, jane@agency.gov",
                "Arlington", "VA", "22202", "US",
                true, now, now);

        Page<OpportunityResponseDTO> page = new PageImpl<>(List.of(opp1, opp2), PageRequest.of(0, 10), 2);
        when(opportunityService.findAll(any(Pageable.class))).thenReturn(page);

        // When/Then
        mockMvc.perform(get("/api/opportunities"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].noticeId").value("NOTICE-001"))
                .andExpect(jsonPath("$.content[1].noticeId").value("NOTICE-002"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void findById_WhenOpportunityExists_ShouldReturnOpportunity() throws Exception {
        // Given
        UUID oppId = UUID.randomUUID();
        Instant now = Instant.now();
        LocalDate today = LocalDate.now();

        OpportunityResponseDTO opp = new OpportunityResponseDTO(
                oppId, "NOTICE-001", "Software Development",
                "SOL-2025-001", UUID.randomUUID(), "Office of IT",
                "Presolicitation", "Combined Synopsis/Solicitation", null, null,
                "541511", "R", "8(a)", today, now.plusSeconds(86400 * 30),
                "Description of opportunity", "https://sam.gov/additional",
                "https://sam.gov/opp/001", "John Doe, john@agency.gov",
                "Washington", "DC", "20500", "US",
                true, now, now);

        when(opportunityService.findById(oppId)).thenReturn(Optional.of(opp));

        // When/Then
        mockMvc.perform(get("/api/opportunities/{id}", oppId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(oppId.toString()))
                .andExpect(jsonPath("$.noticeId").value("NOTICE-001"))
                .andExpect(jsonPath("$.title").value("Software Development"));
    }

    @Test
    void findById_WhenOpportunityNotFound_ShouldReturn404() throws Exception {
        // Given
        UUID oppId = UUID.randomUUID();
        when(opportunityService.findById(oppId)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/api/opportunities/{id}", oppId))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_WithValidData_ShouldReturnCreatedOpportunity() throws Exception {
        // Given
        LocalDate today = LocalDate.now();
        Instant deadline = Instant.now().plusSeconds(86400 * 30);
        UUID agencyId = UUID.randomUUID();

        OpportunityCreateDTO createDTO = new OpportunityCreateDTO(
                "NOTICE-NEW", "New Opportunity", "SOL-NEW",
                agencyId, "Office of Procurement", "Solicitation",
                "Request for Proposal", null, null, "541511",
                "R", "None", today, deadline, "New opportunity description",
                "https://sam.gov/additional", "https://sam.gov/new",
                "Contact Person", "City", "ST", "12345", "US", true);

        Instant now = Instant.now();
        OpportunityResponseDTO createdOpp = new OpportunityResponseDTO(
                UUID.randomUUID(), "NOTICE-NEW", "New Opportunity", "SOL-NEW",
                agencyId, "Office of Procurement", "Solicitation",
                "Request for Proposal", null, null, "541511",
                "R", "None", today, deadline, "New opportunity description",
                "https://sam.gov/additional", "https://sam.gov/new",
                "Contact Person", "City", "ST", "12345", "US", true, now, now);

        when(opportunityService.create(any(OpportunityCreateDTO.class))).thenReturn(createdOpp);

        // When/Then
        mockMvc.perform(post("/api/opportunities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.noticeId").value("NOTICE-NEW"))
                .andExpect(jsonPath("$.title").value("New Opportunity"));
    }

    @Test
    void create_WithInvalidData_ShouldReturn400() throws Exception {
        // Given - missing required noticeId
        OpportunityCreateDTO invalidDTO = new OpportunityCreateDTO(
                null, "Title", "SOL", UUID.randomUUID(), "Office", "Type",
                "Base", null, null, "541511", "R", "None", LocalDate.now(),
                Instant.now(), "Description", "link", "link", "contact",
                "city", "ST", "12345", "US", true);

        // When/Then
        mockMvc.perform(post("/api/opportunities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_WithValidData_ShouldReturnUpdatedOpportunity() throws Exception {
        // Given
        UUID oppId = UUID.randomUUID();
        LocalDate today = LocalDate.now();
        Instant deadline = Instant.now().plusSeconds(86400 * 30);
        UUID agencyId = UUID.randomUUID();

        OpportunityUpdateDTO updateDTO = new OpportunityUpdateDTO(
                "Updated Title", "SOL-UPDATED", agencyId, "Updated Office",
                "Solicitation", "Request for Proposal", null, null, "541511",
                "R", "None", today, deadline, "Updated description",
                "https://sam.gov/updated", "https://sam.gov/ui-updated",
                "Updated Contact", "Updated City", "VA", "22202", "US", true);

        Instant now = Instant.now();
        OpportunityResponseDTO updatedOpp = new OpportunityResponseDTO(
                oppId, "NOTICE-001", "Updated Title", "SOL-UPDATED",
                agencyId, "Updated Office", "Solicitation",
                "Request for Proposal", null, null, "541511",
                "R", "None", today, deadline, "Updated description",
                "https://sam.gov/updated", "https://sam.gov/ui-updated",
                "Updated Contact", "Updated City", "VA", "22202", "US", true, now, now);

        when(opportunityService.update(eq(oppId), any(OpportunityUpdateDTO.class))).thenReturn(updatedOpp);

        // When/Then
        mockMvc.perform(put("/api/opportunities/{id}", oppId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        // Given
        UUID oppId = UUID.randomUUID();

        // When/Then
        mockMvc.perform(delete("/api/opportunities/{id}", oppId))
                .andExpect(status().isNoContent());

        verify(opportunityService).delete(oppId);
    }

    @Test
    void findByNoticeId_WhenExists_ShouldReturnOpportunity() throws Exception {
        // Given
        String noticeId = "NOTICE-001";
        Instant now = Instant.now();
        LocalDate today = LocalDate.now();

        OpportunityResponseDTO opp = new OpportunityResponseDTO(
                UUID.randomUUID(), noticeId, "Software Development",
                "SOL-2025-001", UUID.randomUUID(), "Office of IT",
                "Presolicitation", "Combined Synopsis/Solicitation", null, null,
                "541511", "R", "8(a)", today, now.plusSeconds(86400 * 30),
                "Description", "https://sam.gov/additional",
                "https://sam.gov/opp/001", "John Doe",
                "Washington", "DC", "20500", "US",
                true, now, now);

        when(opportunityService.findByNoticeId(noticeId)).thenReturn(Optional.of(opp));

        // When/Then
        mockMvc.perform(get("/api/opportunities/notice/{noticeId}", noticeId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.noticeId").value(noticeId));
    }

    @Test
    void findByNoticeId_WhenNotFound_ShouldReturn404() throws Exception {
        // Given
        String noticeId = "NOTFOUND";
        when(opportunityService.findByNoticeId(noticeId)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/api/opportunities/notice/{noticeId}", noticeId))
                .andExpect(status().isNotFound());
    }

    @Test
    void findActiveOpportunities_ShouldReturnActiveList() throws Exception {
        // Given
        Instant now = Instant.now();
        LocalDate today = LocalDate.now();

        OpportunityResponseDTO opp1 = new OpportunityResponseDTO(
                UUID.randomUUID(), "NOTICE-001", "Active Opp 1",
                "SOL-001", UUID.randomUUID(), "Office", "Solicitation",
                "RFP", null, null, "541511", "R", "None", today, now.plusSeconds(86400 * 30),
                "Description", "link", "link", "contact",
                "city", "ST", "12345", "US", true, now, now);

        when(opportunityService.findActiveOpportunities()).thenReturn(List.of(opp1));

        // When/Then
        mockMvc.perform(get("/api/opportunities/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].isActive").value(true));
    }

    @Test
    void findByNaicsCode_ShouldReturnOpportunities() throws Exception {
        // Given
        String naicsCode = "541511";
        Instant now = Instant.now();
        LocalDate today = LocalDate.now();

        OpportunityResponseDTO opp = new OpportunityResponseDTO(
                UUID.randomUUID(), "NOTICE-001", "Software Dev",
                "SOL-001", UUID.randomUUID(), "Office", "Solicitation",
                "RFP", null, null, naicsCode, "R", "None", today, now.plusSeconds(86400 * 30),
                "Description", "link", "link", "contact",
                "city", "ST", "12345", "US", true, now, now);

        when(opportunityService.findByNaicsCode(naicsCode)).thenReturn(List.of(opp));

        // When/Then
        mockMvc.perform(get("/api/opportunities/naics/{naicsCode}", naicsCode))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].naicsCode").value(naicsCode));
    }

    @Test
    void findByNoticeType_ShouldReturnOpportunities() throws Exception {
        // Given
        String noticeType = "Solicitation";
        Instant now = Instant.now();
        LocalDate today = LocalDate.now();

        OpportunityResponseDTO opp = new OpportunityResponseDTO(
                UUID.randomUUID(), "NOTICE-001", "Title",
                "SOL-001", UUID.randomUUID(), "Office", noticeType,
                "RFP", null, null, "541511", "R", "None", today, now.plusSeconds(86400 * 30),
                "Description", "link", "link", "contact",
                "city", "ST", "12345", "US", true, now, now);

        when(opportunityService.findByNoticeType(noticeType)).thenReturn(List.of(opp));

        // When/Then
        mockMvc.perform(get("/api/opportunities/notice-type/{noticeType}", noticeType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].noticeType").value(noticeType));
    }

    @Test
    void findByAgency_ShouldReturnOpportunities() throws Exception {
        // Given
        UUID agencyId = UUID.randomUUID();
        Instant now = Instant.now();
        LocalDate today = LocalDate.now();

        OpportunityResponseDTO opp = new OpportunityResponseDTO(
                UUID.randomUUID(), "NOTICE-001", "Title",
                "SOL-001", agencyId, "Office", "Solicitation",
                "RFP", null, null, "541511", "R", "None", today, now.plusSeconds(86400 * 30),
                "Description", "link", "link", "contact",
                "city", "ST", "12345", "US", true, now, now);

        when(opportunityService.findByAgency(agencyId)).thenReturn(List.of(opp));

        // When/Then
        mockMvc.perform(get("/api/opportunities/agency/{agencyId}", agencyId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].agencyId").value(agencyId.toString()));
    }

    @Test
    void findPostedAfter_ShouldReturnOpportunities() throws Exception {
        // Given
        LocalDate date = LocalDate.now().minusDays(7);
        Instant now = Instant.now();
        LocalDate today = LocalDate.now();

        OpportunityResponseDTO opp = new OpportunityResponseDTO(
                UUID.randomUUID(), "NOTICE-001", "Title",
                "SOL-001", UUID.randomUUID(), "Office", "Solicitation",
                "RFP", null, null, "541511", "R", "None", today, now.plusSeconds(86400 * 30),
                "Description", "link", "link", "contact",
                "city", "ST", "12345", "US", true, now, now);

        when(opportunityService.findPostedAfter(date)).thenReturn(List.of(opp));

        // When/Then
        mockMvc.perform(get("/api/opportunities/posted-after")
                        .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void findExpiringBefore_ShouldReturnOpportunities() throws Exception {
        // Given
        Instant deadline = Instant.now().plusSeconds(86400 * 7);
        Instant now = Instant.now();
        LocalDate today = LocalDate.now();

        OpportunityResponseDTO opp = new OpportunityResponseDTO(
                UUID.randomUUID(), "NOTICE-001", "Title",
                "SOL-001", UUID.randomUUID(), "Office", "Solicitation",
                "RFP", null, null, "541511", "R", "None", today, deadline,
                "Description", "link", "link", "contact",
                "city", "ST", "12345", "US", true, now, now);

        when(opportunityService.findExpiringBefore(any(Instant.class))).thenReturn(List.of(opp));

        // When/Then
        mockMvc.perform(get("/api/opportunities/expiring-before")
                        .param("deadline", deadline.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void findUpcomingDeadlines_ShouldReturnOpportunities() throws Exception {
        // Given
        int daysAhead = 30;
        Instant now = Instant.now();
        LocalDate today = LocalDate.now();

        OpportunityResponseDTO opp = new OpportunityResponseDTO(
                UUID.randomUUID(), "NOTICE-001", "Title",
                "SOL-001", UUID.randomUUID(), "Office", "Solicitation",
                "RFP", null, null, "541511", "R", "None", today, now.plusSeconds(86400 * 15),
                "Description", "link", "link", "contact",
                "city", "ST", "12345", "US", true, now, now);

        when(opportunityService.findUpcomingDeadlines(daysAhead)).thenReturn(List.of(opp));

        // When/Then
        mockMvc.perform(get("/api/opportunities/upcoming-deadlines")
                        .param("daysAhead", String.valueOf(daysAhead)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void searchByTitle_ShouldReturnOpportunities() throws Exception {
        // Given
        String searchTerm = "Software";
        Instant now = Instant.now();
        LocalDate today = LocalDate.now();

        OpportunityResponseDTO opp = new OpportunityResponseDTO(
                UUID.randomUUID(), "NOTICE-001", "Software Development",
                "SOL-001", UUID.randomUUID(), "Office", "Solicitation",
                "RFP", null, null, "541511", "R", "None", today, now.plusSeconds(86400 * 30),
                "Description", "link", "link", "contact",
                "city", "ST", "12345", "US", true, now, now);

        when(opportunityService.searchByTitle(searchTerm)).thenReturn(List.of(opp));

        // When/Then
        mockMvc.perform(get("/api/opportunities/search")
                        .param("title", searchTerm))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Software Development"));
    }
}
