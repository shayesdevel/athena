package com.athena.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Opportunity entity representing SAM.gov contract opportunities.
 * Central entity for federal contract discovery and tracking.
 */
@Entity
@Table(name = "opportunities", indexes = {
    @Index(name = "idx_opportunities_notice_id", columnList = "notice_id"),
    @Index(name = "idx_opportunities_agency_id", columnList = "agency_id"),
    @Index(name = "idx_opportunities_naics_code", columnList = "naics_code"),
    @Index(name = "idx_opportunities_notice_type", columnList = "notice_type"),
    @Index(name = "idx_opportunities_posted_date", columnList = "posted_date"),
    @Index(name = "idx_opportunities_response_deadline", columnList = "response_deadline"),
    @Index(name = "idx_opportunities_is_active", columnList = "is_active")
})
public class Opportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Notice ID is required")
    @Size(max = 255)
    @Column(name = "notice_id", nullable = false, unique = true, length = 255)
    private String noticeId;

    @NotBlank(message = "Title is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Size(max = 255)
    @Column(name = "solicitation_number", length = 255)
    private String solicitationNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id")
    private Agency agency;

    @Size(max = 500)
    @Column(name = "office_name", length = 500)
    private String officeName;

    @NotBlank(message = "Notice type is required")
    @Size(max = 50)
    @Column(name = "notice_type", nullable = false, length = 50)
    private String noticeType;

    @Size(max = 50)
    @Column(name = "base_type", length = 50)
    private String baseType;

    @Size(max = 50)
    @Column(name = "archive_type", length = 50)
    private String archiveType;

    @Column(name = "archive_date")
    private LocalDate archiveDate;

    @Size(max = 6)
    @Column(name = "naics_code", length = 6)
    private String naicsCode;

    @Size(max = 10)
    @Column(name = "classification_code", length = 10)
    private String classificationCode;

    @Size(max = 100)
    @Column(name = "set_aside", length = 100)
    private String setAside;

    @Column(name = "posted_date")
    private LocalDate postedDate;

    @Column(name = "response_deadline")
    private Instant responseDeadline;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "additional_info_link", columnDefinition = "TEXT")
    private String additionalInfoLink;

    @Column(name = "ui_link", columnDefinition = "TEXT")
    private String uiLink;

    @Size(max = 255)
    @Column(name = "point_of_contact", length = 255)
    private String pointOfContact;

    @Size(max = 100)
    @Column(name = "place_of_performance_city", length = 100)
    private String placeOfPerformanceCity;

    @Size(max = 2)
    @Column(name = "place_of_performance_state", length = 2)
    private String placeOfPerformanceState;

    @Size(max = 10)
    @Column(name = "place_of_performance_zip", length = 10)
    private String placeOfPerformanceZip;

    @Size(max = 2)
    @Column(name = "place_of_performance_country", length = 2)
    private String placeOfPerformanceCountry = "US";

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

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
        if (isActive == null) {
            isActive = true;
        }
        if (placeOfPerformanceCountry == null) {
            placeOfPerformanceCountry = "US";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // Constructors
    public Opportunity() {
    }

    public Opportunity(String noticeId, String title, String noticeType) {
        this.noticeId = noticeId;
        this.title = title;
        this.noticeType = noticeType;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSolicitationNumber() {
        return solicitationNumber;
    }

    public void setSolicitationNumber(String solicitationNumber) {
        this.solicitationNumber = solicitationNumber;
    }

    public Agency getAgency() {
        return agency;
    }

    public void setAgency(Agency agency) {
        this.agency = agency;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    public String getArchiveType() {
        return archiveType;
    }

    public void setArchiveType(String archiveType) {
        this.archiveType = archiveType;
    }

    public LocalDate getArchiveDate() {
        return archiveDate;
    }

    public void setArchiveDate(LocalDate archiveDate) {
        this.archiveDate = archiveDate;
    }

    public String getNaicsCode() {
        return naicsCode;
    }

    public void setNaicsCode(String naicsCode) {
        this.naicsCode = naicsCode;
    }

    public String getClassificationCode() {
        return classificationCode;
    }

    public void setClassificationCode(String classificationCode) {
        this.classificationCode = classificationCode;
    }

    public String getSetAside() {
        return setAside;
    }

    public void setSetAside(String setAside) {
        this.setAside = setAside;
    }

    public LocalDate getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(LocalDate postedDate) {
        this.postedDate = postedDate;
    }

    public Instant getResponseDeadline() {
        return responseDeadline;
    }

    public void setResponseDeadline(Instant responseDeadline) {
        this.responseDeadline = responseDeadline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdditionalInfoLink() {
        return additionalInfoLink;
    }

    public void setAdditionalInfoLink(String additionalInfoLink) {
        this.additionalInfoLink = additionalInfoLink;
    }

    public String getUiLink() {
        return uiLink;
    }

    public void setUiLink(String uiLink) {
        this.uiLink = uiLink;
    }

    public String getPointOfContact() {
        return pointOfContact;
    }

    public void setPointOfContact(String pointOfContact) {
        this.pointOfContact = pointOfContact;
    }

    public String getPlaceOfPerformanceCity() {
        return placeOfPerformanceCity;
    }

    public void setPlaceOfPerformanceCity(String placeOfPerformanceCity) {
        this.placeOfPerformanceCity = placeOfPerformanceCity;
    }

    public String getPlaceOfPerformanceState() {
        return placeOfPerformanceState;
    }

    public void setPlaceOfPerformanceState(String placeOfPerformanceState) {
        this.placeOfPerformanceState = placeOfPerformanceState;
    }

    public String getPlaceOfPerformanceZip() {
        return placeOfPerformanceZip;
    }

    public void setPlaceOfPerformanceZip(String placeOfPerformanceZip) {
        this.placeOfPerformanceZip = placeOfPerformanceZip;
    }

    public String getPlaceOfPerformanceCountry() {
        return placeOfPerformanceCountry;
    }

    public void setPlaceOfPerformanceCountry(String placeOfPerformanceCountry) {
        this.placeOfPerformanceCountry = placeOfPerformanceCountry;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
        if (!(o instanceof Opportunity)) return false;
        Opportunity that = (Opportunity) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Opportunity{" +
                "id=" + id +
                ", noticeId='" + noticeId + '\'' +
                ", title='" + title + '\'' +
                ", noticeType='" + noticeType + '\'' +
                ", naicsCode='" + naicsCode + '\'' +
                ", postedDate=" + postedDate +
                ", isActive=" + isActive +
                '}';
    }
}
