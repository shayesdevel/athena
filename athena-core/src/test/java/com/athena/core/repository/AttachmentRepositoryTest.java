package com.athena.core.repository;

import com.athena.core.AbstractIntegrationTest;
import com.athena.core.entity.Attachment;
import com.athena.core.entity.Opportunity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for AttachmentRepository using Testcontainers.
 */
@DataJpaTest
class AttachmentRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;

    private Opportunity testOpportunity;

    @BeforeEach
    void setUp() {
        // Create a test opportunity for attachments
        testOpportunity = new Opportunity("TEST-OPP-001", "Test Opportunity", "Solicitation");
        testOpportunity = opportunityRepository.save(testOpportunity);
    }

    @Test
    void shouldSaveAndRetrieveAttachment() {
        // Given
        Attachment attachment = new Attachment(testOpportunity, "Solicitation.pdf", "https://sam.gov/files/123.pdf");
        attachment.setType("solicitation");
        attachment.setMimeType("application/pdf");
        attachment.setFileSize(2048576L);
        attachment.setDescription("Main solicitation document");
        attachment.setSamAttachmentId("SAM-ATT-123");

        // When
        Attachment saved = attachmentRepository.save(attachment);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFileName()).isEqualTo("Solicitation.pdf");
        assertThat(saved.getFileUrl()).isEqualTo("https://sam.gov/files/123.pdf");
        assertThat(saved.getType()).isEqualTo("solicitation");
        assertThat(saved.getMimeType()).isEqualTo("application/pdf");
        assertThat(saved.getFileSize()).isEqualTo(2048576L);
        assertThat(saved.getDescription()).isEqualTo("Main solicitation document");
        assertThat(saved.getSamAttachmentId()).isEqualTo("SAM-ATT-123");
        assertThat(saved.getOpportunity().getId()).isEqualTo(testOpportunity.getId());
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldFindAttachmentsByOpportunity() {
        // Given
        Attachment attachment1 = new Attachment(testOpportunity, "Solicitation.pdf", "https://sam.gov/files/1.pdf");
        Attachment attachment2 = new Attachment(testOpportunity, "Amendment_001.pdf", "https://sam.gov/files/2.pdf");

        attachmentRepository.save(attachment1);
        attachmentRepository.save(attachment2);

        // When
        List<Attachment> attachments = attachmentRepository.findByOpportunity(testOpportunity);

        // Then
        assertThat(attachments).hasSize(2);
        assertThat(attachments).extracting(Attachment::getFileName)
            .containsExactlyInAnyOrder("Solicitation.pdf", "Amendment_001.pdf");
    }

    @Test
    void shouldFindAttachmentsByOpportunityId() {
        // Given
        Attachment attachment1 = new Attachment(testOpportunity, "File1.pdf", "https://sam.gov/files/1.pdf");
        Attachment attachment2 = new Attachment(testOpportunity, "File2.pdf", "https://sam.gov/files/2.pdf");

        attachmentRepository.save(attachment1);
        attachmentRepository.save(attachment2);

        // When
        List<Attachment> attachments = attachmentRepository.findByOpportunityId(testOpportunity.getId());

        // Then
        assertThat(attachments).hasSize(2);
    }

    @Test
    void shouldFindAttachmentsByType() {
        // Given
        Attachment solicitation = new Attachment(testOpportunity, "Solicitation.pdf", "https://sam.gov/files/1.pdf");
        solicitation.setType("solicitation");
        Attachment amendment = new Attachment(testOpportunity, "Amendment.pdf", "https://sam.gov/files/2.pdf");
        amendment.setType("amendment");
        Attachment wageDet = new Attachment(testOpportunity, "WageDet.pdf", "https://sam.gov/files/3.pdf");
        wageDet.setType("wage_determination");

        attachmentRepository.save(solicitation);
        attachmentRepository.save(amendment);
        attachmentRepository.save(wageDet);

        // When
        List<Attachment> amendments = attachmentRepository.findByType("amendment");

        // Then
        assertThat(amendments).hasSize(1);
        assertThat(amendments.get(0).getFileName()).isEqualTo("Amendment.pdf");
    }

    @Test
    void shouldFindAttachmentBySamAttachmentId() {
        // Given
        Attachment attachment = new Attachment(testOpportunity, "Document.pdf", "https://sam.gov/files/123.pdf");
        attachment.setSamAttachmentId("SAM-ATT-XYZ");
        attachmentRepository.save(attachment);

        // When
        Optional<Attachment> found = attachmentRepository.findBySamAttachmentId("SAM-ATT-XYZ");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFileName()).isEqualTo("Document.pdf");
    }

    @Test
    void shouldFindAttachmentsByOpportunityAndType() {
        // Given
        Attachment solicitation = new Attachment(testOpportunity, "Solicitation.pdf", "https://sam.gov/files/1.pdf");
        solicitation.setType("solicitation");
        Attachment amendment1 = new Attachment(testOpportunity, "Amendment_001.pdf", "https://sam.gov/files/2.pdf");
        amendment1.setType("amendment");
        Attachment amendment2 = new Attachment(testOpportunity, "Amendment_002.pdf", "https://sam.gov/files/3.pdf");
        amendment2.setType("amendment");

        attachmentRepository.save(solicitation);
        attachmentRepository.save(amendment1);
        attachmentRepository.save(amendment2);

        // When
        List<Attachment> amendments = attachmentRepository.findByOpportunityAndType(testOpportunity, "amendment");

        // Then
        assertThat(amendments).hasSize(2);
        assertThat(amendments).extracting(Attachment::getFileName)
            .containsExactlyInAnyOrder("Amendment_001.pdf", "Amendment_002.pdf");
    }

    @Test
    void shouldCountAttachmentsByOpportunity() {
        // Given
        Attachment attachment1 = new Attachment(testOpportunity, "File1.pdf", "https://sam.gov/files/1.pdf");
        Attachment attachment2 = new Attachment(testOpportunity, "File2.pdf", "https://sam.gov/files/2.pdf");
        Attachment attachment3 = new Attachment(testOpportunity, "File3.pdf", "https://sam.gov/files/3.pdf");

        attachmentRepository.save(attachment1);
        attachmentRepository.save(attachment2);
        attachmentRepository.save(attachment3);

        // When
        long count = attachmentRepository.countByOpportunity(testOpportunity);

        // Then
        assertThat(count).isEqualTo(3);
    }
}
