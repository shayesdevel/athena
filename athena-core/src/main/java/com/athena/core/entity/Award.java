package com.athena.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Award entity representing contract award data.
 * Captures information about awarded contracts including awardee, value, and dates.
 * Linked to the original opportunity/solicitation.
 */
@Entity
@Table(name = "awards", indexes = {
    @Index(name = "idx_awards_opportunity_id", columnList = "opportunity_id"),
    @Index(name = "idx_awards_organization_id", columnList = "organization_id"),
    @Index(name = "idx_awards_contract_number", columnList = "contract_number"),
    @Index(name = "idx_awards_award_date", columnList = "award_date"),
    @Index(name = "idx_awards_is_active", columnList = "is_active")
})
public class Award {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opportunity_id")
    private Opportunity opportunity;

    @NotBlank(message = "Contract number is required")
    @Size(max = 255)
    @Column(name = "contract_number", nullable = false, unique = true, length = 255)
    private String contractNumber;

    @Size(max = 500)
    @Column(name = "title", length = 500)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization; // Awardee organization

    @Size(max = 500)
    @Column(name = "awardee_name", length = 500)
    private String awardeeName; // Cached name for historical accuracy

    @Size(max = 12)
    @Column(name = "awardee_uei", length = 12)
    private String awardeeUei;

    @Size(max = 9)
    @Column(name = "awardee_duns", length = 9)
    private String awardeeDuns;

    @Column(name = "award_date")
    private LocalDate awardDate;

    @Column(name = "award_amount", precision = 15, scale = 2)
    private BigDecimal awardAmount;

    @Size(max = 3)
    @Column(name = "currency", length = 3)
    private String currency = "USD";

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id")
    private Agency agency; // Awarding agency

    @Size(max = 500)
    @Column(name = "awarding_office", length = 500)
    private String awardingOffice;

    @Size(max = 100)
    @Column(name = "award_type", length = 100)
    private String awardType; // e.g., "Definite", "Indefinite Delivery"

    @Size(max = 6)
    @Column(name = "naics_code", length = 6)
    private String naicsCode;

    @Size(max = 100)
    @Column(name = "set_aside", length = 100)
    private String setAside;

    @Column(columnDefinition = "TEXT")
    private String description;

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
        if (currency == null) {
            currency = "USD";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // Constructors
    public Award() {
    }

    public Award(String contractNumber) {
        this.contractNumber = contractNumber;
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

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public String getAwardeeName() {
        return awardeeName;
    }

    public void setAwardeeName(String awardeeName) {
        this.awardeeName = awardeeName;
    }

    public String getAwardeeUei() {
        return awardeeUei;
    }

    public void setAwardeeUei(String awardeeUei) {
        this.awardeeUei = awardeeUei;
    }

    public String getAwardeeDuns() {
        return awardeeDuns;
    }

    public void setAwardeeDuns(String awardeeDuns) {
        this.awardeeDuns = awardeeDuns;
    }

    public LocalDate getAwardDate() {
        return awardDate;
    }

    public void setAwardDate(LocalDate awardDate) {
        this.awardDate = awardDate;
    }

    public BigDecimal getAwardAmount() {
        return awardAmount;
    }

    public void setAwardAmount(BigDecimal awardAmount) {
        this.awardAmount = awardAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Agency getAgency() {
        return agency;
    }

    public void setAgency(Agency agency) {
        this.agency = agency;
    }

    public String getAwardingOffice() {
        return awardingOffice;
    }

    public void setAwardingOffice(String awardingOffice) {
        this.awardingOffice = awardingOffice;
    }

    public String getAwardType() {
        return awardType;
    }

    public void setAwardType(String awardType) {
        this.awardType = awardType;
    }

    public String getNaicsCode() {
        return naicsCode;
    }

    public void setNaicsCode(String naicsCode) {
        this.naicsCode = naicsCode;
    }

    public String getSetAside() {
        return setAside;
    }

    public void setSetAside(String setAside) {
        this.setAside = setAside;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        if (!(o instanceof Award)) return false;
        Award award = (Award) o;
        return id != null && id.equals(award.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Award{" +
                "id=" + id +
                ", contractNumber='" + contractNumber + '\'' +
                ", awardeeName='" + awardeeName + '\'' +
                ", awardDate=" + awardDate +
                ", awardAmount=" + awardAmount +
                ", isActive=" + isActive +
                '}';
    }
}
