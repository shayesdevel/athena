package com.athena.api.controller;

import com.athena.core.dto.AwardCreateDTO;
import com.athena.core.dto.AwardResponseDTO;
import com.athena.core.dto.AwardUpdateDTO;
import com.athena.core.service.AwardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for Award entity operations.
 * Handles contract award data management and tracking.
 */
@RestController
@RequestMapping("/api/awards")
@Tag(name = "Awards", description = "Contract award management endpoints")
public class AwardController {

    private final AwardService awardService;

    public AwardController(AwardService awardService) {
        this.awardService = awardService;
    }

    @GetMapping
    @Operation(summary = "Get all awards", description = "Retrieve paginated list of all awards")
    public ResponseEntity<Page<AwardResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(awardService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get award by ID", description = "Retrieve a specific award by UUID")
    public ResponseEntity<AwardResponseDTO> findById(@PathVariable UUID id) {
        return awardService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create award", description = "Create a new contract award")
    public ResponseEntity<AwardResponseDTO> create(@RequestBody @Valid AwardCreateDTO createDTO) {
        AwardResponseDTO created = awardService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update award", description = "Update an existing contract award")
    public ResponseEntity<AwardResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid AwardUpdateDTO updateDTO) {
        AwardResponseDTO updated = awardService.update(id, updateDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete award", description = "Soft delete an award (sets isActive to false)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        awardService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/contract/{contractNumber}")
    @Operation(summary = "Find award by contract number", description = "Retrieve award by contract number")
    public ResponseEntity<AwardResponseDTO> findByContractNumber(@PathVariable String contractNumber) {
        return awardService.findByContractNumber(contractNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    @Operation(summary = "Find active awards", description = "Retrieve all active awards")
    public ResponseEntity<List<AwardResponseDTO>> findActiveAwards() {
        return ResponseEntity.ok(awardService.findActiveAwards());
    }

    @GetMapping("/date-range")
    @Operation(summary = "Find awards by date range", description = "Retrieve awards within a specific date range")
    public ResponseEntity<List<AwardResponseDTO>> findByAwardDateBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(awardService.findByAwardDateBetween(startDate, endDate));
    }

    @GetMapping("/naics/{naicsCode}")
    @Operation(summary = "Find awards by NAICS code", description = "Retrieve awards by NAICS code")
    public ResponseEntity<List<AwardResponseDTO>> findByNaicsCode(@PathVariable String naicsCode) {
        return ResponseEntity.ok(awardService.findByNaicsCode(naicsCode));
    }

    @GetMapping("/awardee/{awardeeUei}")
    @Operation(summary = "Find awards by awardee UEI", description = "Retrieve awards by awardee UEI")
    public ResponseEntity<List<AwardResponseDTO>> findByAwardeeUei(@PathVariable String awardeeUei) {
        return ResponseEntity.ok(awardService.findByAwardeeUei(awardeeUei));
    }
}
