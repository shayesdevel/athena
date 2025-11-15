package com.athena.core.service;

import com.athena.core.dto.OpportunityScoreCreateDTO;
import com.athena.core.dto.OpportunityScoreResponseDTO;
import com.athena.core.dto.OpportunityScoreUpdateDTO;
import com.athena.core.entity.OpportunityScore;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.OpportunityScoreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OpportunityScoreServiceImpl implements OpportunityScoreService {
    private final OpportunityScoreRepository opportunityScoreRepository;

    public OpportunityScoreServiceImpl(OpportunityScoreRepository opportunityScoreRepository) {
        this.opportunityScoreRepository = opportunityScoreRepository;
    }

    @Override
    @Transactional
    public OpportunityScoreResponseDTO create(OpportunityScoreCreateDTO dto) {
        OpportunityScore opportunityScore = new OpportunityScore(dto.opportunityId(), dto.scoreType(), dto.scoreValue());
        if (dto.confidence() != null) opportunityScore.setConfidence(dto.confidence());
        if (dto.scoredAt() != null) opportunityScore.setScoredAt(dto.scoredAt());
        if (dto.metadata() != null) opportunityScore.setMetadata(dto.metadata());
        return OpportunityScoreResponseDTO.fromEntity(opportunityScoreRepository.save(opportunityScore));
    }

    @Override
    public Optional<OpportunityScoreResponseDTO> findById(UUID id) {
        return opportunityScoreRepository.findById(id).map(OpportunityScoreResponseDTO::fromEntity);
    }

    @Override
    public Page<OpportunityScoreResponseDTO> findAll(Pageable pageable) {
        return opportunityScoreRepository.findAll(pageable).map(OpportunityScoreResponseDTO::fromEntity);
    }

    @Override
    @Transactional
    public OpportunityScoreResponseDTO update(UUID id, OpportunityScoreUpdateDTO dto) {
        OpportunityScore opportunityScore = opportunityScoreRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("OpportunityScore", id));
        if (dto.scoreType() != null) opportunityScore.setScoreType(dto.scoreType());
        if (dto.scoreValue() != null) opportunityScore.setScoreValue(dto.scoreValue());
        if (dto.confidence() != null) opportunityScore.setConfidence(dto.confidence());
        if (dto.scoredAt() != null) opportunityScore.setScoredAt(dto.scoredAt());
        if (dto.metadata() != null) opportunityScore.setMetadata(dto.metadata());
        return OpportunityScoreResponseDTO.fromEntity(opportunityScoreRepository.save(opportunityScore));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!opportunityScoreRepository.existsById(id)) throw new EntityNotFoundException("OpportunityScore", id);
        opportunityScoreRepository.deleteById(id);
    }

    @Override
    public List<OpportunityScoreResponseDTO> findByOpportunityId(UUID opportunityId) {
        return opportunityScoreRepository.findByOpportunityId(opportunityId).stream()
            .map(OpportunityScoreResponseDTO::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<OpportunityScoreResponseDTO> findByScoreType(String scoreType) {
        return opportunityScoreRepository.findByScoreType(scoreType).stream()
            .map(OpportunityScoreResponseDTO::fromEntity).collect(Collectors.toList());
    }

    @Override
    public Optional<OpportunityScoreResponseDTO> findLatestByOpportunityIdAndScoreType(UUID opportunityId, String scoreType) {
        return opportunityScoreRepository.findLatestByOpportunityIdAndScoreType(opportunityId, scoreType)
            .map(OpportunityScoreResponseDTO::fromEntity);
    }
}
