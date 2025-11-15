package com.athena.core.dto;

import jakarta.validation.constraints.Size;

public record TeamMemberUpdateDTO(
    @Size(max = 100)
    String role,
    String capabilities,
    Boolean isPrime
) {}
