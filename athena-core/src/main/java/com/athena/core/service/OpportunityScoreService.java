package com.athena.core.service;

import com.athena.core.dto.OpportunityScoreCreateDTO;
import com.athena.core.dto.OpportunityScoreResponseDTO;
import com.athena.core.dto.OpportunityScoreUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OpportunityScoreService {
    OpportunityScoreResponseDTO create(OpportunityScoreCreateDTO dto);
    Optional<OpportunityScoreResponseDTO> findById(UUID id);
    Page<OpportunityScoreResponseDTO> findAll(Pageable pageable);
    OpportunityScoreResponseDTO update(UUID id, OpportunityScoreUpdateDTO dto);
    void delete(UUID id);
    List<OpportunityScoreResponseDTO> findByOpportunityId(UUID opportunityId);
    List<OpportunityScoreResponseDTO> findByScoreType(String scoreType);
    Optional<OpportunityScoreResponseDTO> findLatestByOpportunityIdAndScoreType(UUID opportunityId, String scoreType);
}
