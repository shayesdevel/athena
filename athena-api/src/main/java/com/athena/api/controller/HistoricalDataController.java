package com.athena.api.controller;

import com.athena.core.dto.HistoricalDataCreateDTO;
import com.athena.core.dto.HistoricalDataResponseDTO;
import com.athena.core.dto.HistoricalDataUpdateDTO;
import com.athena.core.service.HistoricalDataService;
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
 * REST controller for HistoricalData entity operations.
 * Handles historical snapshots and time-series data tracking.
 */
@RestController
@RequestMapping("/api/historical-data")
@Tag(name = "Historical Data", description = "Historical data tracking endpoints")
public class HistoricalDataController {

    private final HistoricalDataService historicalDataService;

    public HistoricalDataController(HistoricalDataService historicalDataService) {
        this.historicalDataService = historicalDataService;
    }

    @GetMapping
    @Operation(summary = "Get all historical data", description = "Retrieve paginated list of all historical data")
    public ResponseEntity<Page<HistoricalDataResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(historicalDataService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get historical data by ID", description = "Retrieve specific historical data by UUID")
    public ResponseEntity<HistoricalDataResponseDTO> findById(@PathVariable UUID id) {
        return historicalDataService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create historical data", description = "Create new historical data entry")
    public ResponseEntity<HistoricalDataResponseDTO> create(@RequestBody @Valid HistoricalDataCreateDTO createDTO) {
        HistoricalDataResponseDTO created = historicalDataService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update historical data", description = "Update existing historical data")
    public ResponseEntity<HistoricalDataResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid HistoricalDataUpdateDTO updateDTO) {
        HistoricalDataResponseDTO updated = historicalDataService.update(id, updateDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete historical data", description = "Delete historical data entry")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        historicalDataService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/entity/{entityId}")
    @Operation(summary = "Find historical data by entity", description = "Retrieve all historical data for a specific entity")
    public ResponseEntity<List<HistoricalDataResponseDTO>> findByEntityId(@PathVariable UUID entityId) {
        return ResponseEntity.ok(historicalDataService.findByEntityId(entityId));
    }

    @GetMapping("/entity-type/{entityType}/entity/{entityId}")
    @Operation(summary = "Find historical data by entity type and ID", description = "Retrieve historical data by entity type and ID")
    public ResponseEntity<List<HistoricalDataResponseDTO>> findByEntityTypeAndEntityId(
            @PathVariable String entityType,
            @PathVariable UUID entityId) {
        return ResponseEntity.ok(historicalDataService.findByEntityTypeAndEntityId(entityType, entityId));
    }

    @GetMapping("/data-type/{dataType}")
    @Operation(summary = "Find historical data by data type", description = "Retrieve historical data by data type")
    public ResponseEntity<List<HistoricalDataResponseDTO>> findByDataType(@PathVariable String dataType) {
        return ResponseEntity.ok(historicalDataService.findByDataType(dataType));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Find historical data by date range", description = "Retrieve historical data within a date range")
    public ResponseEntity<List<HistoricalDataResponseDTO>> findByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate) {
        return ResponseEntity.ok(historicalDataService.findByDateRange(startDate, endDate));
    }

    @GetMapping("/entity-type/{entityType}")
    @Operation(summary = "Find historical data by entity type", description = "Retrieve historical data by entity type")
    public ResponseEntity<List<HistoricalDataResponseDTO>> findByEntityType(@PathVariable String entityType) {
        return ResponseEntity.ok(historicalDataService.findByEntityType(entityType));
    }
}
