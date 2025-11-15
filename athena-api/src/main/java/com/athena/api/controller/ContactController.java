package com.athena.api.controller;

import com.athena.core.dto.ContactCreateDTO;
import com.athena.core.dto.ContactResponseDTO;
import com.athena.core.dto.ContactUpdateDTO;
import com.athena.core.service.ContactService;
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
 * REST controller for Contact entity operations.
 * Manages points of contact for organizations, agencies, and opportunities.
 */
@RestController
@RequestMapping("/api/contacts")
@Tag(name = "Contacts", description = "Contact management endpoints")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    @Operation(summary = "Get all contacts", description = "Retrieve paginated list of all contacts")
    public ResponseEntity<Page<ContactResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(contactService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get contact by ID", description = "Retrieve a specific contact by UUID")
    public ResponseEntity<ContactResponseDTO> findById(@PathVariable UUID id) {
        return contactService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create contact", description = "Create a new contact")
    public ResponseEntity<ContactResponseDTO> create(@RequestBody @Valid ContactCreateDTO createDTO) {
        ContactResponseDTO created = contactService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update contact", description = "Update an existing contact")
    public ResponseEntity<ContactResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid ContactUpdateDTO updateDTO) {
        ContactResponseDTO updated = contactService.update(id, updateDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete contact", description = "Delete a contact")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        contactService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Find contact by email", description = "Retrieve contact by email address")
    public ResponseEntity<ContactResponseDTO> findByEmail(@PathVariable String email) {
        return contactService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/organization/{organizationId}")
    @Operation(summary = "Find contacts by organization", description = "Retrieve all contacts for an organization")
    public ResponseEntity<List<ContactResponseDTO>> findByOrganization(@PathVariable UUID organizationId) {
        return ResponseEntity.ok(contactService.findByOrganization(organizationId));
    }

    @GetMapping("/agency/{agencyId}")
    @Operation(summary = "Find contacts by agency", description = "Retrieve all contacts for an agency")
    public ResponseEntity<List<ContactResponseDTO>> findByAgency(@PathVariable UUID agencyId) {
        return ResponseEntity.ok(contactService.findByAgency(agencyId));
    }

    @GetMapping("/opportunity/{opportunityId}")
    @Operation(summary = "Find contacts by opportunity", description = "Retrieve all contacts for an opportunity")
    public ResponseEntity<List<ContactResponseDTO>> findByOpportunity(@PathVariable UUID opportunityId) {
        return ResponseEntity.ok(contactService.findByOpportunity(opportunityId));
    }

    @GetMapping("/organization/{organizationId}/primary")
    @Operation(summary = "Find primary contact for organization", description = "Retrieve primary contact for an organization")
    public ResponseEntity<ContactResponseDTO> findPrimaryContactForOrganization(@PathVariable UUID organizationId) {
        return contactService.findPrimaryContactForOrganization(organizationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/agency/{agencyId}/primary")
    @Operation(summary = "Find primary contact for agency", description = "Retrieve primary contact for an agency")
    public ResponseEntity<ContactResponseDTO> findPrimaryContactForAgency(@PathVariable UUID agencyId) {
        return contactService.findPrimaryContactForAgency(agencyId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/opportunity/{opportunityId}/primary")
    @Operation(summary = "Find primary contact for opportunity", description = "Retrieve primary contact for an opportunity")
    public ResponseEntity<ContactResponseDTO> findPrimaryContactForOpportunity(@PathVariable UUID opportunityId) {
        return contactService.findPrimaryContactForOpportunity(opportunityId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{contactType}")
    @Operation(summary = "Find contacts by type", description = "Retrieve contacts by contact type")
    public ResponseEntity<List<ContactResponseDTO>> findByContactType(@PathVariable String contactType) {
        return ResponseEntity.ok(contactService.findByContactType(contactType));
    }
}
