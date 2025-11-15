package com.athena.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

/**
 * Organization entity representing contractor organizations.
 * Stores SAM.gov registration data and socioeconomic certifications.
 */
@Entity
@Table(name = "organizations", indexes = {
    @Index(name = "idx_organizations_uei", columnList = "uei"),
    @Index(name = "idx_organizations_cage_code", columnList = "cage_code"),
    @Index(name = "idx_organizations_name", columnList = "name"),
    @Index(name = "idx_organizations_primary_naics", columnList = "primary_naics")
})
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Organization name is required")
    @Size(max = 500)
    @Column(nullable = false, length = 500)
    private String name;

    @Size(max = 12)
    @Column(unique = true, length = 12)
    private String uei;

    @Size(max = 5)
    @Column(name = "cage_code", length = 5)
    private String cageCode;

    @Size(max = 9)
    @Column(length = 9)
    private String duns;

    @Column(name = "sam_url", columnDefinition = "TEXT")
    private String samUrl;

    @Size(max = 6)
    @Column(name = "primary_naics", length = 6)
    private String primaryNaics;

    @Size(max = 100)
    @Column(name = "business_type", length = 100)
    private String businessType;

    @Column(name = "is_small_business")
    private Boolean isSmallBusiness = false;

    @Column(name = "is_woman_owned")
    private Boolean isWomanOwned = false;

    @Column(name = "is_veteran_owned")
    private Boolean isVeteranOwned = false;

    @Column(name = "is_8a_certified")
    private Boolean is8aCertified = false;

    @Size(max = 500)
    @Column(name = "street_address", length = 500)
    private String streetAddress;

    @Size(max = 100)
    @Column(length = 100)
    private String city;

    @Size(max = 2)
    @Column(name = "state_code", length = 2)
    private String stateCode;

    @Size(max = 10)
    @Column(name = "zip_code", length = 10)
    private String zipCode;

    @Size(max = 2)
    @Column(name = "country_code", length = 2)
    private String countryCode = "US";

    @Column(name = "website_url", columnDefinition = "TEXT")
    private String websiteUrl;

    @Size(max = 20)
    @Column(length = 20)
    private String phone;

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
        if (countryCode == null) {
            countryCode = "US";
        }
        if (isSmallBusiness == null) isSmallBusiness = false;
        if (isWomanOwned == null) isWomanOwned = false;
        if (isVeteranOwned == null) isVeteranOwned = false;
        if (is8aCertified == null) is8aCertified = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // Constructors
    public Organization() {
    }

    public Organization(String name) {
        this.name = name;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUei() {
        return uei;
    }

    public void setUei(String uei) {
        this.uei = uei;
    }

    public String getCageCode() {
        return cageCode;
    }

    public void setCageCode(String cageCode) {
        this.cageCode = cageCode;
    }

    public String getDuns() {
        return duns;
    }

    public void setDuns(String duns) {
        this.duns = duns;
    }

    public String getSamUrl() {
        return samUrl;
    }

    public void setSamUrl(String samUrl) {
        this.samUrl = samUrl;
    }

    public String getPrimaryNaics() {
        return primaryNaics;
    }

    public void setPrimaryNaics(String primaryNaics) {
        this.primaryNaics = primaryNaics;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public Boolean getIsSmallBusiness() {
        return isSmallBusiness;
    }

    public void setIsSmallBusiness(Boolean isSmallBusiness) {
        this.isSmallBusiness = isSmallBusiness;
    }

    public Boolean getIsWomanOwned() {
        return isWomanOwned;
    }

    public void setIsWomanOwned(Boolean isWomanOwned) {
        this.isWomanOwned = isWomanOwned;
    }

    public Boolean getIsVeteranOwned() {
        return isVeteranOwned;
    }

    public void setIsVeteranOwned(Boolean isVeteranOwned) {
        this.isVeteranOwned = isVeteranOwned;
    }

    public Boolean getIs8aCertified() {
        return is8aCertified;
    }

    public void setIs8aCertified(Boolean is8aCertified) {
        this.is8aCertified = is8aCertified;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
        if (!(o instanceof Organization)) return false;
        Organization that = (Organization) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Organization{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", uei='" + uei + '\'' +
                ", cageCode='" + cageCode + '\'' +
                ", primaryNaics='" + primaryNaics + '\'' +
                ", isSmallBusiness=" + isSmallBusiness +
                '}';
    }
}
