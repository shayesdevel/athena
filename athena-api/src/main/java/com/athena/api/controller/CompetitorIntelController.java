package com.athena.api.controller;

import com.athena.core.dto.CompetitorIntelCreateDTO;
import com.athena.core.dto.CompetitorIntelResponseDTO;
import com.athena.core.dto.CompetitorIntelUpdateDTO;
import com.athena.core.service.CompetitorIntelService;
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
 * REST controller for CompetitorIntel entity operations.
 * Handles competitive intelligence tracking and analysis.
 */
@RestController
@RequestMapping("/api/competitor-intel")
@Tag(name = "Competitor Intelligence", description = "Competitive intelligence endpoints")
public class CompetitorIntelController {

    private final CompetitorIntelService competitorIntelService;

    public CompetitorIntelController(CompetitorIntelService competitorIntelService) {
        this.competitorIntelService = competitorIntelService;
    }

    @GetMapping
    @Operation(summary = "Get all competitor intel", description = "Retrieve paginated list of all competitor intelligence")
    public ResponseEntity<Page<CompetitorIntelResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(competitorIntelService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get competitor intel by ID", description = "Retrieve specific competitor intelligence by UUID")
    public ResponseEntity<CompetitorIntelResponseDTO> findById(@PathVariable UUID id) {
        return competitorIntelService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create competitor intel", description = "Create new competitor intelligence entry")
    public ResponseEntity<CompetitorIntelResponseDTO> create(@RequestBody @Valid CompetitorIntelCreateDTO createDTO) {
        CompetitorIntelResponseDTO created = competitorIntelService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update competitor intel", description = "Update existing competitor intelligence")
    public ResponseEntity<CompetitorIntelResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid CompetitorIntelUpdateDTO updateDTO) {
        CompetitorIntelResponseDTO updated = competitorIntelService.update(id, updateDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete competitor intel", description = "Delete competitor intelligence entry")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        competitorIntelService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/opportunity/{opportunityId}")
    @Operation(summary = "Find intel by opportunity", description = "Retrieve competitor intelligence for a specific opportunity")
    public ResponseEntity<List<CompetitorIntelResponseDTO>> findByOpportunityId(@PathVariable UUID opportunityId) {
        return ResponseEntity.ok(competitorIntelService.findByOpportunityId(opportunityId));
    }

    @GetMapping("/organization/{organizationId}")
    @Operation(summary = "Find intel by organization", description = "Retrieve competitor intelligence for a specific organization")
    public ResponseEntity<List<CompetitorIntelResponseDTO>> findByOrganizationId(@PathVariable UUID organizationId) {
        return ResponseEntity.ok(competitorIntelService.findByOrganizationId(organizationId));
    }

    @GetMapping("/likelihood/{likelihood}")
    @Operation(summary = "Find intel by likelihood", description = "Retrieve competitor intelligence by likelihood level")
    public ResponseEntity<List<CompetitorIntelResponseDTO>> findByLikelihood(@PathVariable String likelihood) {
        return ResponseEntity.ok(competitorIntelService.findByLikelihood(likelihood));
    }
}
