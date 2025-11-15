package com.athena.core.service;

import com.athena.core.dto.CompetitorIntelCreateDTO;
import com.athena.core.dto.CompetitorIntelResponseDTO;
import com.athena.core.dto.CompetitorIntelUpdateDTO;
import com.athena.core.entity.CompetitorIntel;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.CompetitorIntelRepository;
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
public class CompetitorIntelServiceImpl implements CompetitorIntelService {
    private final CompetitorIntelRepository competitorIntelRepository;

    public CompetitorIntelServiceImpl(CompetitorIntelRepository competitorIntelRepository) {
        this.competitorIntelRepository = competitorIntelRepository;
    }

    @Override
    @Transactional
    public CompetitorIntelResponseDTO create(CompetitorIntelCreateDTO dto) {
        CompetitorIntel competitorIntel = new CompetitorIntel(dto.organizationId(), dto.opportunityId(), dto.source());
        if (dto.likelihood() != null) competitorIntel.setLikelihood(dto.likelihood());
        if (dto.strengths() != null) competitorIntel.setStrengths(dto.strengths());
        if (dto.weaknesses() != null) competitorIntel.setWeaknesses(dto.weaknesses());
        return CompetitorIntelResponseDTO.fromEntity(competitorIntelRepository.save(competitorIntel));
    }

    @Override
    public Optional<CompetitorIntelResponseDTO> findById(UUID id) {
        return competitorIntelRepository.findById(id).map(CompetitorIntelResponseDTO::fromEntity);
    }

    @Override
    public Page<CompetitorIntelResponseDTO> findAll(Pageable pageable) {
        return competitorIntelRepository.findAll(pageable).map(CompetitorIntelResponseDTO::fromEntity);
    }

    @Override
    @Transactional
    public CompetitorIntelResponseDTO update(UUID id, CompetitorIntelUpdateDTO dto) {
        CompetitorIntel competitorIntel = competitorIntelRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("CompetitorIntel", id));
        if (dto.likelihood() != null) competitorIntel.setLikelihood(dto.likelihood());
        if (dto.strengths() != null) competitorIntel.setStrengths(dto.strengths());
        if (dto.weaknesses() != null) competitorIntel.setWeaknesses(dto.weaknesses());
        if (dto.source() != null) competitorIntel.setSource(dto.source());
        return CompetitorIntelResponseDTO.fromEntity(competitorIntelRepository.save(competitorIntel));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!competitorIntelRepository.existsById(id)) throw new EntityNotFoundException("CompetitorIntel", id);
        competitorIntelRepository.deleteById(id);
    }

    @Override
    public List<CompetitorIntelResponseDTO> findByOpportunityId(UUID opportunityId) {
        return competitorIntelRepository.findByOpportunityId(opportunityId).stream()
            .map(CompetitorIntelResponseDTO::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<CompetitorIntelResponseDTO> findByOrganizationId(UUID organizationId) {
        return competitorIntelRepository.findByOrganizationId(organizationId).stream()
            .map(CompetitorIntelResponseDTO::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<CompetitorIntelResponseDTO> findByLikelihood(String likelihood) {
        return competitorIntelRepository.findByLikelihood(likelihood).stream()
            .map(CompetitorIntelResponseDTO::fromEntity).collect(Collectors.toList());
    }
}
