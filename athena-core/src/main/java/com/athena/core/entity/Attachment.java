package com.athena.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

/**
 * Attachment entity representing document attachments for opportunities.
 * Stores metadata for solicitation documents, amendments, Q&A files, etc.
 * Examples: "Solicitation.pdf", "Amendment_001.pdf", "Wage_Determination.pdf"
 */
@Entity
@Table(name = "attachments", indexes = {
    @Index(name = "idx_attachments_opportunity_id", columnList = "opportunity_id"),
    @Index(name = "idx_attachments_type", columnList = "type"),
    @Index(name = "idx_attachments_created_at", columnList = "created_at")
})
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Opportunity is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opportunity_id", nullable = false)
    private Opportunity opportunity;

    @NotBlank(message = "File name is required")
    @Size(max = 500)
    @Column(name = "file_name", nullable = false, length = 500)
    private String fileName;

    @NotBlank(message = "File URL is required")
    @Column(name = "file_url", nullable = false, columnDefinition = "TEXT")
    private String fileUrl;

    @Size(max = 50)
    @Column(name = "type", length = 50)
    private String type; // e.g., "solicitation", "amendment", "wage_determination", "qa"

    @Size(max = 100)
    @Column(name = "mime_type", length = 100)
    private String mimeType; // e.g., "application/pdf", "application/msword"

    @Column(name = "file_size")
    private Long fileSize; // in bytes

    @Column(columnDefinition = "TEXT")
    private String description;

    @Size(max = 50)
    @Column(name = "sam_attachment_id", length = 50)
    private String samAttachmentId; // SAM.gov's internal attachment ID

    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // Constructors
    public Attachment() {
    }

    public Attachment(Opportunity opportunity, String fileName, String fileUrl) {
        this.opportunity = opportunity;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Opportunity getOpportunity() {
        return opportunity;
    }

    public void setOpportunity(Opportunity opportunity) {
        this.opportunity = opportunity;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSamAttachmentId() {
        return samAttachmentId;
    }

    public void setSamAttachmentId(String samAttachmentId) {
        this.samAttachmentId = samAttachmentId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Attachment)) return false;
        Attachment that = (Attachment) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", type='" + type + '\'' +
                ", fileSize=" + fileSize +
                '}';
    }
}
