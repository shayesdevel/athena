package com.athena.api.controller;

import com.athena.core.dto.AlertCreateDTO;
import com.athena.core.dto.AlertResponseDTO;
import com.athena.core.dto.AlertUpdateDTO;
import com.athena.core.service.AlertService;
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
 * REST controller for Alert entity operations.
 * Handles user notification preferences and alert triggers.
 */
@RestController
@RequestMapping("/api/alerts")
@Tag(name = "Alerts", description = "User alert management endpoints")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    @Operation(summary = "Get all alerts", description = "Retrieve paginated list of all alerts")
    public ResponseEntity<Page<AlertResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(alertService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get alert by ID", description = "Retrieve a specific alert by UUID")
    public ResponseEntity<AlertResponseDTO> findById(@PathVariable UUID id) {
        return alertService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create alert", description = "Create a new alert")
    public ResponseEntity<AlertResponseDTO> create(@RequestBody @Valid AlertCreateDTO createDTO) {
        AlertResponseDTO created = alertService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update alert", description = "Update an existing alert")
    public ResponseEntity<AlertResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid AlertUpdateDTO updateDTO) {
        AlertResponseDTO updated = alertService.update(id, updateDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete alert", description = "Soft delete an alert (sets isActive to false)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        alertService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Find alerts by user", description = "Retrieve all alerts for a specific user")
    public ResponseEntity<List<AlertResponseDTO>> findByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(alertService.findByUserId(userId));
    }

    @GetMapping("/user/{userId}/active")
    @Operation(summary = "Find active alerts by user", description = "Retrieve active alerts for a specific user")
    public ResponseEntity<List<AlertResponseDTO>> findActiveByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(alertService.findActiveByUserId(userId));
    }

    @GetMapping("/type/{alertType}")
    @Operation(summary = "Find alerts by type", description = "Retrieve alerts by alert type")
    public ResponseEntity<List<AlertResponseDTO>> findByAlertType(@PathVariable String alertType) {
        return ResponseEntity.ok(alertService.findByAlertType(alertType));
    }

    @PostMapping("/{id}/trigger")
    @Operation(summary = "Record alert trigger", description = "Record that an alert was triggered")
    public ResponseEntity<Void> recordTrigger(@PathVariable UUID id) {
        alertService.recordTrigger(id);
        return ResponseEntity.noContent().build();
    }
}
