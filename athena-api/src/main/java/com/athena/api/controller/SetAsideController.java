package com.athena.api.controller;

import com.athena.core.dto.SetAsideCreateDTO;
import com.athena.core.dto.SetAsideResponseDTO;
import com.athena.core.dto.SetAsideUpdateDTO;
import com.athena.core.service.SetAsideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for SetAside entity operations.
 * Handles contract set-aside types (e.g., Small Business, 8(a), HUBZone).
 */
@RestController
@RequestMapping("/api/set-asides")
@Tag(name = "Set-Asides", description = "Contract set-aside type management endpoints")
public class SetAsideController {

    private final SetAsideService setAsideService;

    public SetAsideController(SetAsideService setAsideService) {
        this.setAsideService = setAsideService;
    }

    @GetMapping
    @Operation(summary = "Get all set-asides", description = "Retrieve paginated list of all set-aside types")
    public ResponseEntity<Page<SetAsideResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(setAsideService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get set-aside by ID", description = "Retrieve a specific set-aside type by UUID")
    public ResponseEntity<SetAsideResponseDTO> findById(@PathVariable UUID id) {
        return setAsideService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create set-aside", description = "Create a new set-aside type")
    public ResponseEntity<SetAsideResponseDTO> create(@RequestBody @Valid SetAsideCreateDTO createDTO) {
        SetAsideResponseDTO created = setAsideService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update set-aside", description = "Update an existing set-aside type")
    public ResponseEntity<SetAsideResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid SetAsideUpdateDTO updateDTO) {
        SetAsideResponseDTO updated = setAsideService.update(id, updateDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete set-aside", description = "Soft delete a set-aside type (sets isActive to false)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        setAsideService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
