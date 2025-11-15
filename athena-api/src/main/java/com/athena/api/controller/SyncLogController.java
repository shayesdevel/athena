package com.athena.api.controller;

import com.athena.core.dto.SyncLogCreateDTO;
import com.athena.core.dto.SyncLogResponseDTO;
import com.athena.core.dto.SyncLogUpdateDTO;
import com.athena.core.service.SyncLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for SyncLog entity operations.
 * Handles SAM.gov synchronization tracking and audit logs.
 */
@RestController
@RequestMapping("/api/sync-logs")
@Tag(name = "Sync Logs", description = "SAM.gov synchronization log endpoints")
public class SyncLogController {

    private final SyncLogService syncLogService;

    public SyncLogController(SyncLogService syncLogService) {
        this.syncLogService = syncLogService;
    }

    @GetMapping
    @Operation(summary = "Get all sync logs", description = "Retrieve paginated list of all sync logs")
    public ResponseEntity<Page<SyncLogResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(syncLogService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get sync log by ID", description = "Retrieve specific sync log by UUID")
    public ResponseEntity<SyncLogResponseDTO> findById(@PathVariable UUID id) {
        return syncLogService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create sync log", description = "Create new sync log entry")
    public ResponseEntity<SyncLogResponseDTO> create(@RequestBody @Valid SyncLogCreateDTO createDTO) {
        SyncLogResponseDTO created = syncLogService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update sync log", description = "Update existing sync log")
    public ResponseEntity<SyncLogResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid SyncLogUpdateDTO updateDTO) {
        SyncLogResponseDTO updated = syncLogService.update(id, updateDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete sync log", description = "Delete sync log entry")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        syncLogService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Find sync logs by status", description = "Retrieve sync logs by status")
    public ResponseEntity<List<SyncLogResponseDTO>> findByStatus(@PathVariable String status) {
        return ResponseEntity.ok(syncLogService.findByStatus(status));
    }

    @GetMapping("/sync-type/{syncType}")
    @Operation(summary = "Find sync logs by sync type", description = "Retrieve sync logs by sync type")
    public ResponseEntity<List<SyncLogResponseDTO>> findBySyncType(@PathVariable String syncType) {
        return ResponseEntity.ok(syncLogService.findBySyncType(syncType));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Find sync logs by date range", description = "Retrieve sync logs within a date range")
    public ResponseEntity<List<SyncLogResponseDTO>> findByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate) {
        return ResponseEntity.ok(syncLogService.findByDateRange(startDate, endDate));
    }

    @GetMapping("/recent")
    @Operation(summary = "Find recent sync logs", description = "Retrieve most recent sync logs")
    public ResponseEntity<List<SyncLogResponseDTO>> findRecentSyncs(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(syncLogService.findRecentSyncs(limit));
    }

    @GetMapping("/failed")
    @Operation(summary = "Find failed sync logs", description = "Retrieve all failed sync logs")
    public ResponseEntity<List<SyncLogResponseDTO>> findFailedSyncs() {
        return ResponseEntity.ok(syncLogService.findFailedSyncs());
    }
}
