package com.athena.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

/**
 * ContractVehicle entity representing contract acquisition vehicles.
 * Examples: "GSA Schedule", "IDIQ", "BPA", "GWAC", "Single Award", etc.
 * Reference data for contract types and acquisition methods.
 */
@Entity
@Table(name = "contract_vehicles", indexes = {
    @Index(name = "idx_contract_vehicles_code", columnList = "code"),
    @Index(name = "idx_contract_vehicles_is_active", columnList = "is_active"),
    @Index(name = "idx_contract_vehicles_category", columnList = "category")
})
public class ContractVehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Contract vehicle code is required")
    @Size(max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @NotBlank(message = "Contract vehicle name is required")
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Size(max = 50)
    @Column(name = "category", length = 50)
    private String category; // e.g., "Schedule", "IDIQ", "BPA", "GWAC"

    @Size(max = 255)
    @Column(name = "managing_agency", length = 255)
    private String managingAgency; // e.g., "GSA", "VA", "DHS"

    @Column(name = "url", columnDefinition = "TEXT")
    private String url;

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
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // Constructors
    public ContractVehicle() {
    }

    public ContractVehicle(String code, String name) {
        this.code = code;
        this.name = name;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getManagingAgency() {
        return managingAgency;
    }

    public void setManagingAgency(String managingAgency) {
        this.managingAgency = managingAgency;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
        if (!(o instanceof ContractVehicle)) return false;
        ContractVehicle that = (ContractVehicle) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ContractVehicle{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
