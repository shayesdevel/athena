package com.athena.core.dto;

import com.athena.core.entity.ContractVehicle;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for contract vehicle response.
 */
public record ContractVehicleResponseDTO(
    UUID id,
    String code,
    String name,
    String description,
    Boolean isActive,
    String category,
    String managingAgency,
    String url,
    Instant createdAt,
    Instant updatedAt
) {
    /**
     * Create a ContractVehicleResponseDTO from a ContractVehicle entity.
     */
    public static ContractVehicleResponseDTO fromEntity(ContractVehicle contractVehicle) {
        return new ContractVehicleResponseDTO(
            contractVehicle.getId(),
            contractVehicle.getCode(),
            contractVehicle.getName(),
            contractVehicle.getDescription(),
            contractVehicle.getIsActive(),
            contractVehicle.getCategory(),
            contractVehicle.getManagingAgency(),
            contractVehicle.getUrl(),
            contractVehicle.getCreatedAt(),
            contractVehicle.getUpdatedAt()
        );
    }
}
