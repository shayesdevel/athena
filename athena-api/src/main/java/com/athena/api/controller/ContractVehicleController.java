package com.athena.api.controller;

import com.athena.core.dto.ContractVehicleCreateDTO;
import com.athena.core.dto.ContractVehicleResponseDTO;
import com.athena.core.dto.ContractVehicleUpdateDTO;
import com.athena.core.service.ContractVehicleService;
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
 * REST controller for ContractVehicle entity operations.
 * Handles contract acquisition vehicles (e.g., GSA Schedule, IDIQ, BPA).
 */
@RestController
@RequestMapping("/api/contract-vehicles")
@Tag(name = "Contract Vehicles", description = "Contract vehicle management endpoints")
public class ContractVehicleController {

    private final ContractVehicleService contractVehicleService;

    public ContractVehicleController(ContractVehicleService contractVehicleService) {
        this.contractVehicleService = contractVehicleService;
    }

    @GetMapping
    @Operation(summary = "Get all contract vehicles", description = "Retrieve paginated list of all contract vehicles")
    public ResponseEntity<Page<ContractVehicleResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(contractVehicleService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get contract vehicle by ID", description = "Retrieve a specific contract vehicle by UUID")
    public ResponseEntity<ContractVehicleResponseDTO> findById(@PathVariable UUID id) {
        return contractVehicleService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create contract vehicle", description = "Create a new contract vehicle")
    public ResponseEntity<ContractVehicleResponseDTO> create(@RequestBody @Valid ContractVehicleCreateDTO createDTO) {
        ContractVehicleResponseDTO created = contractVehicleService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update contract vehicle", description = "Update an existing contract vehicle")
    public ResponseEntity<ContractVehicleResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid ContractVehicleUpdateDTO updateDTO) {
        ContractVehicleResponseDTO updated = contractVehicleService.update(id, updateDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete contract vehicle", description = "Soft delete a contract vehicle (sets isActive to false)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        contractVehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
