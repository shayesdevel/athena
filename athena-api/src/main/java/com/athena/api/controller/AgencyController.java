package com.athena.api.controller;

import com.athena.core.dto.AgencyCreateDTO;
import com.athena.core.dto.AgencyResponseDTO;
import com.athena.core.dto.AgencyUpdateDTO;
import com.athena.core.service.AgencyService;
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
 * REST controller for Agency entity operations.
 * Manages federal government agencies and hierarchical relationships.
 */
@RestController
@RequestMapping("/api/agencies")
@Tag(name = "Agencies", description = "Federal agency management endpoints")
public class AgencyController {

    private final AgencyService agencyService;

    public AgencyController(AgencyService agencyService) {
        this.agencyService = agencyService;
    }

    @GetMapping
    @Operation(summary = "Get all agencies", description = "Retrieve paginated list of all agencies")
    public ResponseEntity<Page<AgencyResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(agencyService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get agency by ID", description = "Retrieve a specific agency by UUID")
    public ResponseEntity<AgencyResponseDTO> findById(@PathVariable UUID id) {
        return agencyService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create agency", description = "Create a new federal agency")
    public ResponseEntity<AgencyResponseDTO> create(@RequestBody @Valid AgencyCreateDTO createDTO) {
        AgencyResponseDTO created = agencyService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update agency", description = "Update an existing federal agency")
    public ResponseEntity<AgencyResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid AgencyUpdateDTO updateDTO) {
        AgencyResponseDTO updated = agencyService.update(id, updateDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete agency", description = "Soft delete an agency (sets isActive to false)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        agencyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/abbreviation/{abbreviation}")
    @Operation(summary = "Find agency by abbreviation", description = "Retrieve agency by abbreviation")
    public ResponseEntity<AgencyResponseDTO> findByAbbreviation(@PathVariable String abbreviation) {
        return agencyService.findByAbbreviation(abbreviation)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @Operation(summary = "Search agencies by name", description = "Search agencies by partial name match")
    public ResponseEntity<List<AgencyResponseDTO>> searchByName(@RequestParam String name) {
        return ResponseEntity.ok(agencyService.searchByName(name));
    }

    @GetMapping("/active")
    @Operation(summary = "Find active agencies", description = "Retrieve all active agencies")
    public ResponseEntity<List<AgencyResponseDTO>> findActiveAgencies() {
        return ResponseEntity.ok(agencyService.findActiveAgencies());
    }

    @GetMapping("/{parentAgencyId}/sub-agencies")
    @Operation(summary = "Find sub-agencies", description = "Retrieve sub-agencies by parent agency ID")
    public ResponseEntity<List<AgencyResponseDTO>> findSubAgencies(@PathVariable UUID parentAgencyId) {
        return ResponseEntity.ok(agencyService.findSubAgencies(parentAgencyId));
    }

    @GetMapping("/department/{department}")
    @Operation(summary = "Find agencies by department", description = "Retrieve agencies by department name")
    public ResponseEntity<List<AgencyResponseDTO>> findByDepartment(@PathVariable String department) {
        return ResponseEntity.ok(agencyService.findByDepartment(department));
    }
}
