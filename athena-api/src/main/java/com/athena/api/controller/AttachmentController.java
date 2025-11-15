package com.athena.api.controller;

import com.athena.core.dto.AttachmentCreateDTO;
import com.athena.core.dto.AttachmentResponseDTO;
import com.athena.core.dto.AttachmentUpdateDTO;
import com.athena.core.service.AttachmentService;
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
 * REST controller for Attachment entity operations.
 * Handles document attachment management for opportunities.
 */
@RestController
@RequestMapping("/api/attachments")
@Tag(name = "Attachments", description = "Document attachment management endpoints")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @GetMapping
    @Operation(summary = "Get all attachments", description = "Retrieve paginated list of all attachments")
    public ResponseEntity<Page<AttachmentResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(attachmentService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get attachment by ID", description = "Retrieve a specific attachment by UUID")
    public ResponseEntity<AttachmentResponseDTO> findById(@PathVariable UUID id) {
        return attachmentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create attachment", description = "Create a new attachment")
    public ResponseEntity<AttachmentResponseDTO> create(@RequestBody @Valid AttachmentCreateDTO createDTO) {
        AttachmentResponseDTO created = attachmentService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update attachment", description = "Update an existing attachment")
    public ResponseEntity<AttachmentResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid AttachmentUpdateDTO updateDTO) {
        AttachmentResponseDTO updated = attachmentService.update(id, updateDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete attachment", description = "Hard delete an attachment")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        attachmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/opportunity/{opportunityId}")
    @Operation(summary = "Find attachments by opportunity", description = "Retrieve all attachments for a specific opportunity")
    public ResponseEntity<List<AttachmentResponseDTO>> findByOpportunityId(@PathVariable UUID opportunityId) {
        return ResponseEntity.ok(attachmentService.findByOpportunityId(opportunityId));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Find attachments by type", description = "Retrieve attachments by type (e.g., solicitation, amendment)")
    public ResponseEntity<List<AttachmentResponseDTO>> findByType(@PathVariable String type) {
        return ResponseEntity.ok(attachmentService.findByType(type));
    }

    @GetMapping("/sam/{samAttachmentId}")
    @Operation(summary = "Find attachment by SAM.gov ID", description = "Retrieve attachment by SAM.gov attachment ID")
    public ResponseEntity<AttachmentResponseDTO> findBySamAttachmentId(@PathVariable String samAttachmentId) {
        return attachmentService.findBySamAttachmentId(samAttachmentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
