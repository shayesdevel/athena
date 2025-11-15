package com.athena.api.controller;

import com.athena.core.dto.OrganizationCreateDTO;
import com.athena.core.dto.OrganizationResponseDTO;
import com.athena.core.dto.OrganizationUpdateDTO;
import com.athena.core.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for Organization entity operations.
 * Manages contractor organizations and SAM.gov registration data.
 */
@RestController
@RequestMapping("/api/organizations")
@Tag(name = "Organizations", description = "Organization management endpoints")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping
    @Operation(summary = "Get all organizations", description = "Retrieve paginated list of all organizations")
    public ResponseEntity<Page<OrganizationResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(organizationService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get organization by ID", description = "Retrieve a specific organization by UUID")
    public ResponseEntity<OrganizationResponseDTO> findById(@PathVariable UUID id) {
        return organizationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create organization", description = "Create a new organization")
    public ResponseEntity<OrganizationResponseDTO> create(@RequestBody @Valid OrganizationCreateDTO createDTO) {
        OrganizationResponseDTO created = organizationService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update organization", description = "Update an existing organization")
    public ResponseEntity<OrganizationResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid OrganizationUpdateDTO updateDTO) {
        OrganizationResponseDTO updated = organizationService.update(id, updateDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete organization", description = "Delete an organization")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        organizationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/uei/{uei}")
    @Operation(summary = "Find organization by UEI", description = "Retrieve organization by Unique Entity Identifier")
    public ResponseEntity<OrganizationResponseDTO> findByUei(@PathVariable String uei) {
        return organizationService.findByUei(uei)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cage/{cageCode}")
    @Operation(summary = "Find organization by CAGE code", description = "Retrieve organization by CAGE code")
    public ResponseEntity<OrganizationResponseDTO> findByCageCode(@PathVariable String cageCode) {
        return organizationService.findByCageCode(cageCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @Operation(summary = "Search organizations by name", description = "Search organizations by partial name match")
    public ResponseEntity<List<OrganizationResponseDTO>> searchByName(@RequestParam String name) {
        return ResponseEntity.ok(organizationService.searchByName(name));
    }

    @GetMapping("/naics/{primaryNaics}")
    @Operation(summary = "Find organizations by primary NAICS", description = "Retrieve organizations by primary NAICS code")
    public ResponseEntity<List<OrganizationResponseDTO>> findByPrimaryNaics(@PathVariable String primaryNaics) {
        return ResponseEntity.ok(organizationService.findByPrimaryNaics(primaryNaics));
    }

    @GetMapping("/small-business")
    @Operation(summary = "Find small businesses", description = "Retrieve all small business organizations")
    public ResponseEntity<List<OrganizationResponseDTO>> findSmallBusinesses() {
        return ResponseEntity.ok(organizationService.findSmallBusinesses());
    }
}
