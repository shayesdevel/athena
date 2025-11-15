package com.athena.api.controller;

import com.athena.core.dto.OpportunityCreateDTO;
import com.athena.core.dto.OpportunityResponseDTO;
import com.athena.core.dto.OpportunityUpdateDTO;
import com.athena.core.service.OpportunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for Opportunity entity operations.
 * Manages SAM.gov contract opportunities and related data.
 */
@RestController
@RequestMapping("/api/opportunities")
@Tag(name = "Opportunities", description = "Contract opportunity management endpoints")
public class OpportunityController {

    private final OpportunityService opportunityService;

    public OpportunityController(OpportunityService opportunityService) {
        this.opportunityService = opportunityService;
    }

    @GetMapping
    @Operation(summary = "Get all opportunities", description = "Retrieve paginated list of all opportunities")
    public ResponseEntity<Page<OpportunityResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(opportunityService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get opportunity by ID", description = "Retrieve a specific opportunity by UUID")
    public ResponseEntity<OpportunityResponseDTO> findById(@PathVariable UUID id) {
        return opportunityService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create opportunity", description = "Create a new contract opportunity")
    public ResponseEntity<OpportunityResponseDTO> create(@RequestBody @Valid OpportunityCreateDTO createDTO) {
        OpportunityResponseDTO created = opportunityService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update opportunity", description = "Update an existing contract opportunity")
    public ResponseEntity<OpportunityResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid OpportunityUpdateDTO updateDTO) {
        OpportunityResponseDTO updated = opportunityService.update(id, updateDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete opportunity", description = "Soft delete an opportunity (sets isActive to false)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        opportunityService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/notice/{noticeId}")
    @Operation(summary = "Find opportunity by notice ID", description = "Retrieve opportunity by SAM.gov notice ID")
    public ResponseEntity<OpportunityResponseDTO> findByNoticeId(@PathVariable String noticeId) {
        return opportunityService.findByNoticeId(noticeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    @Operation(summary = "Find active opportunities", description = "Retrieve all active opportunities")
    public ResponseEntity<List<OpportunityResponseDTO>> findActiveOpportunities() {
        return ResponseEntity.ok(opportunityService.findActiveOpportunities());
    }

    @GetMapping("/naics/{naicsCode}")
    @Operation(summary = "Find opportunities by NAICS code", description = "Retrieve opportunities by NAICS code")
    public ResponseEntity<List<OpportunityResponseDTO>> findByNaicsCode(@PathVariable String naicsCode) {
        return ResponseEntity.ok(opportunityService.findByNaicsCode(naicsCode));
    }

    @GetMapping("/notice-type/{noticeType}")
    @Operation(summary = "Find opportunities by notice type", description = "Retrieve opportunities by notice type")
    public ResponseEntity<List<OpportunityResponseDTO>> findByNoticeType(@PathVariable String noticeType) {
        return ResponseEntity.ok(opportunityService.findByNoticeType(noticeType));
    }

    @GetMapping("/agency/{agencyId}")
    @Operation(summary = "Find opportunities by agency", description = "Retrieve opportunities from a specific agency")
    public ResponseEntity<List<OpportunityResponseDTO>> findByAgency(@PathVariable UUID agencyId) {
        return ResponseEntity.ok(opportunityService.findByAgency(agencyId));
    }

    @GetMapping("/posted-after")
    @Operation(summary = "Find opportunities posted after date", description = "Retrieve opportunities posted after a specific date")
    public ResponseEntity<List<OpportunityResponseDTO>> findPostedAfter(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(opportunityService.findPostedAfter(date));
    }

    @GetMapping("/expiring-before")
    @Operation(summary = "Find opportunities expiring before instant", description = "Retrieve opportunities with deadline before a specific instant")
    public ResponseEntity<List<OpportunityResponseDTO>> findExpiringBefore(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant deadline) {
        return ResponseEntity.ok(opportunityService.findExpiringBefore(deadline));
    }

    @GetMapping("/upcoming-deadlines")
    @Operation(summary = "Find opportunities with upcoming deadlines", description = "Retrieve active opportunities with deadlines in the next N days")
    public ResponseEntity<List<OpportunityResponseDTO>> findUpcomingDeadlines(@RequestParam int daysAhead) {
        return ResponseEntity.ok(opportunityService.findUpcomingDeadlines(daysAhead));
    }

    @GetMapping("/search")
    @Operation(summary = "Search opportunities by title", description = "Search opportunities by partial title match")
    public ResponseEntity<List<OpportunityResponseDTO>> searchByTitle(@RequestParam String title) {
        return ResponseEntity.ok(opportunityService.searchByTitle(title));
    }
}
