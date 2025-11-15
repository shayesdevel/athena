package com.athena.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a new contract vehicle.
 */
public record ContractVehicleCreateDTO(
    @NotBlank(message = "Contract vehicle code is required")
    @Size(max = 50)
    String code,

    @NotBlank(message = "Contract vehicle name is required")
    @Size(max = 255)
    String name,

    String description,

    @Size(max = 50)
    String category,

    @Size(max = 255)
    String managingAgency,

    String url
) {
}
