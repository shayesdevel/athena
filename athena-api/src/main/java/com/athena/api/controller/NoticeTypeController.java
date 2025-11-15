package com.athena.api.controller;

import com.athena.core.dto.NoticeTypeCreateDTO;
import com.athena.core.dto.NoticeTypeResponseDTO;
import com.athena.core.dto.NoticeTypeUpdateDTO;
import com.athena.core.service.NoticeTypeService;
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
 * REST controller for NoticeType entity operations.
 * Handles SAM.gov notice type management (e.g., Presolicitation, Award Notice).
 */
@RestController
@RequestMapping("/api/notice-types")
@Tag(name = "Notice Types", description = "SAM.gov notice type management endpoints")
public class NoticeTypeController {

    private final NoticeTypeService noticeTypeService;

    public NoticeTypeController(NoticeTypeService noticeTypeService) {
        this.noticeTypeService = noticeTypeService;
    }

    @GetMapping
    @Operation(summary = "Get all notice types", description = "Retrieve paginated list of all notice types")
    public ResponseEntity<Page<NoticeTypeResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(noticeTypeService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get notice type by ID", description = "Retrieve a specific notice type by UUID")
    public ResponseEntity<NoticeTypeResponseDTO> findById(@PathVariable UUID id) {
        return noticeTypeService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create notice type", description = "Create a new notice type")
    public ResponseEntity<NoticeTypeResponseDTO> create(@RequestBody @Valid NoticeTypeCreateDTO createDTO) {
        NoticeTypeResponseDTO created = noticeTypeService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update notice type", description = "Update an existing notice type")
    public ResponseEntity<NoticeTypeResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid NoticeTypeUpdateDTO updateDTO) {
        NoticeTypeResponseDTO updated = noticeTypeService.update(id, updateDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete notice type", description = "Soft delete a notice type (sets isActive to false)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        noticeTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
