package com.athena.core.dto;

import jakarta.validation.constraints.Size;

/**
 * DTO for updating an existing contract vehicle.
 */
public record ContractVehicleUpdateDTO(
    @Size(max = 255)
    String name,

    String description,

    @Size(max = 50)
    String category,

    @Size(max = 255)
    String managingAgency,

    String url,

    Boolean isActive
) {
}
