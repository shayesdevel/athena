package com.athena.api.controller;

import com.athena.core.dto.SavedSearchCreateDTO;
import com.athena.core.dto.SavedSearchResponseDTO;
import com.athena.core.dto.SavedSearchUpdateDTO;
import com.athena.core.service.SavedSearchService;
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
 * REST controller for SavedSearch entity operations.
 * Handles user-saved search queries for opportunities.
 */
@RestController
@RequestMapping("/api/saved-searches")
@Tag(name = "Saved Searches", description = "Saved search management endpoints")
public class SavedSearchController {

    private final SavedSearchService savedSearchService;

    public SavedSearchController(SavedSearchService savedSearchService) {
        this.savedSearchService = savedSearchService;
    }

    @GetMapping
    @Operation(summary = "Get all saved searches", description = "Retrieve paginated list of all saved searches")
    public ResponseEntity<Page<SavedSearchResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(savedSearchService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get saved search by ID", description = "Retrieve a specific saved search by UUID")
    public ResponseEntity<SavedSearchResponseDTO> findById(@PathVariable UUID id) {
        return savedSearchService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create saved search", description = "Create a new saved search")
    public ResponseEntity<SavedSearchResponseDTO> create(@RequestBody @Valid SavedSearchCreateDTO createDTO) {
        SavedSearchResponseDTO created = savedSearchService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update saved search", description = "Update an existing saved search")
    public ResponseEntity<SavedSearchResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid SavedSearchUpdateDTO updateDTO) {
        SavedSearchResponseDTO updated = savedSearchService.update(id, updateDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete saved search", description = "Soft delete a saved search (sets isActive to false)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        savedSearchService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Find saved searches by user", description = "Retrieve all saved searches for a specific user")
    public ResponseEntity<List<SavedSearchResponseDTO>> findByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(savedSearchService.findByUserId(userId));
    }

    @GetMapping("/user/{userId}/active")
    @Operation(summary = "Find active saved searches by user", description = "Retrieve active saved searches for a specific user")
    public ResponseEntity<List<SavedSearchResponseDTO>> findActiveByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(savedSearchService.findActiveByUserId(userId));
    }

    @PostMapping("/{id}/execute")
    @Operation(summary = "Record search execution", description = "Record execution of a saved search")
    public ResponseEntity<Void> recordExecution(@PathVariable UUID id) {
        savedSearchService.recordExecution(id);
        return ResponseEntity.noContent().build();
    }
}
