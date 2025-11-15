package com.athena.api.controller;

import com.athena.core.dto.TeamCreateDTO;
import com.athena.core.dto.TeamResponseDTO;
import com.athena.core.dto.TeamUpdateDTO;
import com.athena.core.service.TeamService;
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
 * REST controller for Team entity operations.
 * Handles contractor teaming arrangements and collaboration.
 */
@RestController
@RequestMapping("/api/teams")
@Tag(name = "Teams", description = "Team management endpoints")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    @Operation(summary = "Get all teams", description = "Retrieve paginated list of all teams")
    public ResponseEntity<Page<TeamResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(teamService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get team by ID", description = "Retrieve a specific team by UUID")
    public ResponseEntity<TeamResponseDTO> findById(@PathVariable UUID id) {
        return teamService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create team", description = "Create a new team")
    public ResponseEntity<TeamResponseDTO> create(@RequestBody @Valid TeamCreateDTO createDTO) {
        TeamResponseDTO created = teamService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update team", description = "Update an existing team")
    public ResponseEntity<TeamResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid TeamUpdateDTO updateDTO) {
        TeamResponseDTO updated = teamService.update(id, updateDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete team", description = "Delete a team")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        teamService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/lead-organization/{leadOrganizationId}")
    @Operation(summary = "Find teams by lead organization", description = "Retrieve teams by lead organization ID")
    public ResponseEntity<List<TeamResponseDTO>> findByLeadOrganizationId(@PathVariable UUID leadOrganizationId) {
        return ResponseEntity.ok(teamService.findByLeadOrganizationId(leadOrganizationId));
    }

    @GetMapping("/opportunity/{opportunityId}")
    @Operation(summary = "Find teams by opportunity", description = "Retrieve teams for a specific opportunity")
    public ResponseEntity<List<TeamResponseDTO>> findByOpportunityId(@PathVariable UUID opportunityId) {
        return ResponseEntity.ok(teamService.findByOpportunityId(opportunityId));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Find teams by status", description = "Retrieve teams by status")
    public ResponseEntity<List<TeamResponseDTO>> findByStatus(@PathVariable String status) {
        return ResponseEntity.ok(teamService.findByStatus(status));
    }

    @GetMapping("/created-by/{createdBy}")
    @Operation(summary = "Find teams created by user", description = "Retrieve teams created by a specific user")
    public ResponseEntity<List<TeamResponseDTO>> findByCreatedBy(@PathVariable UUID createdBy) {
        return ResponseEntity.ok(teamService.findByCreatedBy(createdBy));
    }
}
