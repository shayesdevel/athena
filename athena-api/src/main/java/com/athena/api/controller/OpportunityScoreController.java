package com.athena.api.controller;

import com.athena.core.dto.OpportunityScoreCreateDTO;
import com.athena.core.dto.OpportunityScoreResponseDTO;
import com.athena.core.dto.OpportunityScoreUpdateDTO;
import com.athena.core.service.OpportunityScoreService;
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
 * REST controller for OpportunityScore entity operations.
 * Handles AI-powered opportunity scoring and analysis.
 */
@RestController
@RequestMapping("/api/opportunity-scores")
@Tag(name = "Opportunity Scores", description = "AI opportunity scoring endpoints")
public class OpportunityScoreController {

    private final OpportunityScoreService opportunityScoreService;

    public OpportunityScoreController(OpportunityScoreService opportunityScoreService) {
        this.opportunityScoreService = opportunityScoreService;
    }

    @GetMapping
    @Operation(summary = "Get all opportunity scores", description = "Retrieve paginated list of all opportunity scores")
    public ResponseEntity<Page<OpportunityScoreResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(opportunityScoreService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get opportunity score by ID", description = "Retrieve a specific opportunity score by UUID")
    public ResponseEntity<OpportunityScoreResponseDTO> findById(@PathVariable UUID id) {
        return opportunityScoreService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create opportunity score", description = "Create a new opportunity score")
    public ResponseEntity<OpportunityScoreResponseDTO> create(@RequestBody @Valid OpportunityScoreCreateDTO createDTO) {
        OpportunityScoreResponseDTO created = opportunityScoreService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update opportunity score", description = "Update an existing opportunity score")
    public ResponseEntity<OpportunityScoreResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid OpportunityScoreUpdateDTO updateDTO) {
        OpportunityScoreResponseDTO updated = opportunityScoreService.update(id, updateDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete opportunity score", description = "Delete an opportunity score")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        opportunityScoreService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/opportunity/{opportunityId}")
    @Operation(summary = "Find scores by opportunity", description = "Retrieve all scores for a specific opportunity")
    public ResponseEntity<List<OpportunityScoreResponseDTO>> findByOpportunityId(@PathVariable UUID opportunityId) {
        return ResponseEntity.ok(opportunityScoreService.findByOpportunityId(opportunityId));
    }

    @GetMapping("/type/{scoreType}")
    @Operation(summary = "Find scores by type", description = "Retrieve all scores by score type")
    public ResponseEntity<List<OpportunityScoreResponseDTO>> findByScoreType(@PathVariable String scoreType) {
        return ResponseEntity.ok(opportunityScoreService.findByScoreType(scoreType));
    }

    @GetMapping("/opportunity/{opportunityId}/type/{scoreType}/latest")
    @Operation(summary = "Find latest score by opportunity and type", description = "Retrieve latest score for opportunity and score type")
    public ResponseEntity<OpportunityScoreResponseDTO> findLatestByOpportunityIdAndScoreType(
            @PathVariable UUID opportunityId,
            @PathVariable String scoreType) {
        return opportunityScoreService.findLatestByOpportunityIdAndScoreType(opportunityId, scoreType)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
