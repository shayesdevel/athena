package com.athena.core.dto;

import jakarta.validation.constraints.Size;

public record CompetitorIntelUpdateDTO(
    @Size(max = 50)
    String likelihood,
    String strengths,
    String weaknesses,
    @Size(max = 100)
    String source
) {}
