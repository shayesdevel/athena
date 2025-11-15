package com.athena.api.controller;

import com.athena.core.dto.NaicsCreateDTO;
import com.athena.core.dto.NaicsResponseDTO;
import com.athena.core.dto.NaicsUpdateDTO;
import com.athena.core.service.NaicsService;
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
 * REST controller for NAICS entity operations.
 * Handles North American Industry Classification System codes.
 */
@RestController
@RequestMapping("/api/naics")
@Tag(name = "NAICS", description = "NAICS code management endpoints")
public class NaicsController {

    private final NaicsService naicsService;

    public NaicsController(NaicsService naicsService) {
        this.naicsService = naicsService;
    }

    @GetMapping
    @Operation(summary = "Get all NAICS codes", description = "Retrieve paginated list of all NAICS codes")
    public ResponseEntity<Page<NaicsResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(naicsService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get NAICS code by ID", description = "Retrieve a specific NAICS code by UUID")
    public ResponseEntity<NaicsResponseDTO> findById(@PathVariable UUID id) {
        return naicsService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create NAICS code", description = "Create a new NAICS code")
    public ResponseEntity<NaicsResponseDTO> create(@RequestBody @Valid NaicsCreateDTO createDTO) {
        NaicsResponseDTO created = naicsService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update NAICS code", description = "Update an existing NAICS code")
    public ResponseEntity<NaicsResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid NaicsUpdateDTO updateDTO) {
        NaicsResponseDTO updated = naicsService.update(id, updateDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete NAICS code", description = "Soft delete a NAICS code (sets isActive to false)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        naicsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
