package com.athena.core.service;

import com.athena.core.dto.AttachmentCreateDTO;
import com.athena.core.dto.AttachmentResponseDTO;
import com.athena.core.dto.AttachmentUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for Attachment entity operations.
 * Handles document attachment management for opportunities.
 */
public interface AttachmentService {

    /**
     * Create a new attachment.
     *
     * @param dto the attachment creation data
     * @return the created attachment
     * @throws com.athena.core.exception.EntityNotFoundException if opportunity not found
     * @throws com.athena.core.exception.DuplicateEntityException if samAttachmentId already exists
     */
    AttachmentResponseDTO create(AttachmentCreateDTO dto);

    /**
     * Find attachment by ID.
     *
     * @param id the attachment UUID
     * @return Optional containing the attachment if found
     */
    Optional<AttachmentResponseDTO> findById(UUID id);

    /**
     * Find all attachments with pagination.
     *
     * @param pageable pagination parameters
     * @return page of attachments
     */
    Page<AttachmentResponseDTO> findAll(Pageable pageable);

    /**
     * Update an existing attachment.
     *
     * @param id the attachment UUID
     * @param dto the update data
     * @return the updated attachment
     * @throws com.athena.core.exception.EntityNotFoundException if attachment not found
     */
    AttachmentResponseDTO update(UUID id, AttachmentUpdateDTO dto);

    /**
     * Delete an attachment (hard delete as attachments are immutable once created).
     *
     * @param id the attachment UUID
     * @throws com.athena.core.exception.EntityNotFoundException if attachment not found
     */
    void delete(UUID id);

    /**
     * Find all attachments for a specific opportunity.
     *
     * @param opportunityId the opportunity UUID
     * @return list of attachments for the opportunity
     */
    List<AttachmentResponseDTO> findByOpportunityId(UUID opportunityId);

    /**
     * Find attachments by type.
     *
     * @param type the attachment type (e.g., "solicitation", "amendment")
     * @return list of attachments of the specified type
     */
    List<AttachmentResponseDTO> findByType(String type);

    /**
     * Find attachment by SAM.gov attachment ID.
     *
     * @param samAttachmentId the SAM.gov attachment ID
     * @return Optional containing the attachment if found
     */
    Optional<AttachmentResponseDTO> findBySamAttachmentId(String samAttachmentId);

    /**
     * Check if SAM attachment ID already exists.
     *
     * @param samAttachmentId the SAM attachment ID to check
     * @return true if exists, false otherwise
     */
    boolean existsBySamAttachmentId(String samAttachmentId);
}
