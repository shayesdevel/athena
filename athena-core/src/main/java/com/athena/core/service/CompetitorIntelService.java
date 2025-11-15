package com.athena.core.service;

import com.athena.core.dto.CompetitorIntelCreateDTO;
import com.athena.core.dto.CompetitorIntelResponseDTO;
import com.athena.core.dto.CompetitorIntelUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompetitorIntelService {
    CompetitorIntelResponseDTO create(CompetitorIntelCreateDTO dto);
    Optional<CompetitorIntelResponseDTO> findById(UUID id);
    Page<CompetitorIntelResponseDTO> findAll(Pageable pageable);
    CompetitorIntelResponseDTO update(UUID id, CompetitorIntelUpdateDTO dto);
    void delete(UUID id);
    List<CompetitorIntelResponseDTO> findByOpportunityId(UUID opportunityId);
    List<CompetitorIntelResponseDTO> findByOrganizationId(UUID organizationId);
    List<CompetitorIntelResponseDTO> findByLikelihood(String likelihood);
}
