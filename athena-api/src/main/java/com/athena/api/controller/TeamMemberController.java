package com.athena.api.controller;

import com.athena.core.dto.TeamMemberCreateDTO;
import com.athena.core.dto.TeamMemberResponseDTO;
import com.athena.core.dto.TeamMemberUpdateDTO;
import com.athena.core.service.TeamMemberService;
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
 * REST controller for TeamMember entity operations.
 * Handles team member management and roles.
 */
@RestController
@RequestMapping("/api/team-members")
@Tag(name = "Team Members", description = "Team member management endpoints")
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    public TeamMemberController(TeamMemberService teamMemberService) {
        this.teamMemberService = teamMemberService;
    }

    @GetMapping
    @Operation(summary = "Get all team members", description = "Retrieve paginated list of all team members")
    public ResponseEntity<Page<TeamMemberResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(teamMemberService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get team member by ID", description = "Retrieve a specific team member by UUID")
    public ResponseEntity<TeamMemberResponseDTO> findById(@PathVariable UUID id) {
        return teamMemberService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create team member", description = "Create a new team member")
    public ResponseEntity<TeamMemberResponseDTO> create(@RequestBody @Valid TeamMemberCreateDTO createDTO) {
        TeamMemberResponseDTO created = teamMemberService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update team member", description = "Update an existing team member")
    public ResponseEntity<TeamMemberResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid TeamMemberUpdateDTO updateDTO) {
        TeamMemberResponseDTO updated = teamMemberService.update(id, updateDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete team member", description = "Delete a team member")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        teamMemberService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/team/{teamId}")
    @Operation(summary = "Find team members by team", description = "Retrieve all members of a specific team")
    public ResponseEntity<List<TeamMemberResponseDTO>> findByTeamId(@PathVariable UUID teamId) {
        return ResponseEntity.ok(teamMemberService.findByTeamId(teamId));
    }

    @GetMapping("/organization/{organizationId}")
    @Operation(summary = "Find team members by organization", description = "Retrieve all team members from a specific organization")
    public ResponseEntity<List<TeamMemberResponseDTO>> findByOrganizationId(@PathVariable UUID organizationId) {
        return ResponseEntity.ok(teamMemberService.findByOrganizationId(organizationId));
    }

    @GetMapping("/team/{teamId}/prime-contractors")
    @Operation(summary = "Find prime contractors by team", description = "Retrieve prime contractors for a specific team")
    public ResponseEntity<List<TeamMemberResponseDTO>> findPrimeContractorsByTeamId(@PathVariable UUID teamId) {
        return ResponseEntity.ok(teamMemberService.findPrimeContractorsByTeamId(teamId));
    }
}
