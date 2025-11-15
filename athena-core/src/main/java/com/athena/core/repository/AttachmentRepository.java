package com.athena.core.repository;

import com.athena.core.entity.Attachment;
import com.athena.core.entity.Opportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Attachment entity operations.
 * Provides CRUD operations and custom queries for attachment management.
 */
@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {

    /**
     * Find all attachments for a specific opportunity.
     *
     * @param opportunity the opportunity to find attachments for
     * @return list of attachments for the opportunity
     */
    List<Attachment> findByOpportunity(Opportunity opportunity);

    /**
     * Find all attachments for a specific opportunity by ID.
     *
     * @param opportunityId the opportunity ID
     * @return list of attachments for the opportunity
     */
    List<Attachment> findByOpportunityId(UUID opportunityId);

    /**
     * Find attachments by type.
     *
     * @param type the attachment type (e.g., "solicitation", "amendment")
     * @return list of attachments of the specified type
     */
    List<Attachment> findByType(String type);

    /**
     * Find attachment by SAM.gov attachment ID.
     *
     * @param samAttachmentId the SAM.gov attachment ID
     * @return Optional containing the attachment if found
     */
    Optional<Attachment> findBySamAttachmentId(String samAttachmentId);

    /**
     * Find attachments by opportunity and type.
     *
     * @param opportunity the opportunity
     * @param type the attachment type
     * @return list of attachments matching both criteria
     */
    List<Attachment> findByOpportunityAndType(Opportunity opportunity, String type);

    /**
     * Count attachments for a specific opportunity.
     *
     * @param opportunity the opportunity
     * @return number of attachments for the opportunity
     */
    long countByOpportunity(Opportunity opportunity);
}
