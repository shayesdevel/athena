package com.athena.core.service;

import com.athena.core.dto.OpportunityCreateDTO;
import com.athena.core.dto.OpportunityResponseDTO;
import com.athena.core.dto.OpportunityUpdateDTO;
import com.athena.core.entity.Agency;
import com.athena.core.entity.Opportunity;
import com.athena.core.exception.DuplicateEntityException;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.AgencyRepository;
import com.athena.core.repository.OpportunityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of OpportunityService.
 */
@Service
@Transactional(readOnly = true)
public class OpportunityServiceImpl implements OpportunityService {

    private final OpportunityRepository opportunityRepository;
    private final AgencyRepository agencyRepository;

    public OpportunityServiceImpl(OpportunityRepository opportunityRepository,
                                  AgencyRepository agencyRepository) {
        this.opportunityRepository = opportunityRepository;
        this.agencyRepository = agencyRepository;
    }

    @Override
    @Transactional
    public OpportunityResponseDTO create(OpportunityCreateDTO dto) {
        // Check for duplicate notice ID
        if (opportunityRepository.existsByNoticeId(dto.noticeId())) {
            throw new DuplicateEntityException("Opportunity", "noticeId", dto.noticeId());
        }

        // Create opportunity entity
        Opportunity opp = new Opportunity(dto.noticeId(), dto.title(), dto.noticeType());

        // Set agency if provided
        if (dto.agencyId() != null) {
            Agency agency = agencyRepository.findById(dto.agencyId())
                .orElseThrow(() -> new EntityNotFoundException("Agency", dto.agencyId()));
            opp.setAgency(agency);
        }

        // Map remaining fields
        mapCreateDtoToEntity(dto, opp);

        Opportunity savedOpp = opportunityRepository.save(opp);
        return OpportunityResponseDTO.fromEntity(savedOpp);
    }

    @Override
    public Optional<OpportunityResponseDTO> findById(UUID id) {
        return opportunityRepository.findById(id)
            .map(OpportunityResponseDTO::fromEntity);
    }

    @Override
    public Page<OpportunityResponseDTO> findAll(Pageable pageable) {
        return opportunityRepository.findAll(pageable)
            .map(OpportunityResponseDTO::fromEntity);
    }

    @Override
    @Transactional
    public OpportunityResponseDTO update(UUID id, OpportunityUpdateDTO dto) {
        Opportunity opp = opportunityRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Opportunity", id));

        // Update agency if provided
        if (dto.agencyId() != null) {
            Agency agency = agencyRepository.findById(dto.agencyId())
                .orElseThrow(() -> new EntityNotFoundException("Agency", dto.agencyId()));
            opp.setAgency(agency);
        }

        // Update remaining fields
        updateEntityFromDto(dto, opp);

        Opportunity updatedOpp = opportunityRepository.save(opp);
        return OpportunityResponseDTO.fromEntity(updatedOpp);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Opportunity opp = opportunityRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Opportunity", id));

        // Soft delete
        opp.setIsActive(false);
        opportunityRepository.save(opp);
    }

    @Override
    public Optional<OpportunityResponseDTO> findByNoticeId(String noticeId) {
        return opportunityRepository.findByNoticeId(noticeId)
            .map(OpportunityResponseDTO::fromEntity);
    }

    @Override
    public List<OpportunityResponseDTO> findActiveOpportunities() {
        return opportunityRepository.findByIsActiveTrue()
            .stream()
            .map(OpportunityResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<OpportunityResponseDTO> findByNaicsCode(String naicsCode) {
        return opportunityRepository.findByNaicsCode(naicsCode)
            .stream()
            .map(OpportunityResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<OpportunityResponseDTO> findByNoticeType(String noticeType) {
        return opportunityRepository.findByNoticeType(noticeType)
            .stream()
            .map(OpportunityResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<OpportunityResponseDTO> findByAgency(UUID agencyId) {
        return opportunityRepository.findByAgencyId(agencyId)
            .stream()
            .map(OpportunityResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<OpportunityResponseDTO> findPostedAfter(LocalDate date) {
        return opportunityRepository.findByPostedDateAfter(date)
            .stream()
            .map(OpportunityResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<OpportunityResponseDTO> findExpiringBefore(Instant deadline) {
        return opportunityRepository.findByResponseDeadlineBefore(deadline)
            .stream()
            .map(OpportunityResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<OpportunityResponseDTO> findUpcomingDeadlines(int daysAhead) {
        Instant now = Instant.now();
        Instant futureDeadline = now.plus(daysAhead, ChronoUnit.DAYS);

        return opportunityRepository.findActiveOpportunitiesWithUpcomingDeadlines(now, futureDeadline)
            .stream()
            .map(OpportunityResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<OpportunityResponseDTO> searchByTitle(String title) {
        return opportunityRepository.findByTitleContainingIgnoreCase(title)
            .stream()
            .map(OpportunityResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public boolean existsByNoticeId(String noticeId) {
        return opportunityRepository.existsByNoticeId(noticeId);
    }

    /**
     * Map create DTO fields to entity.
     */
    private void mapCreateDtoToEntity(OpportunityCreateDTO dto, Opportunity opp) {
        opp.setSolicitationNumber(dto.solicitationNumber());
        opp.setOfficeName(dto.officeName());
        opp.setBaseType(dto.baseType());
        opp.setArchiveType(dto.archiveType());
        opp.setArchiveDate(dto.archiveDate());
        opp.setNaicsCode(dto.naicsCode());
        opp.setClassificationCode(dto.classificationCode());
        opp.setSetAside(dto.setAside());
        opp.setPostedDate(dto.postedDate());
        opp.setResponseDeadline(dto.responseDeadline());
        opp.setDescription(dto.description());
        opp.setAdditionalInfoLink(dto.additionalInfoLink());
        opp.setUiLink(dto.uiLink());
        opp.setPointOfContact(dto.pointOfContact());
        opp.setPlaceOfPerformanceCity(dto.placeOfPerformanceCity());
        opp.setPlaceOfPerformanceState(dto.placeOfPerformanceState());
        opp.setPlaceOfPerformanceZip(dto.placeOfPerformanceZip());
        opp.setPlaceOfPerformanceCountry(dto.placeOfPerformanceCountry() != null ? dto.placeOfPerformanceCountry() : "US");
        opp.setIsActive(dto.isActive() != null ? dto.isActive() : true);
    }

    /**
     * Update entity fields from update DTO (only non-null fields).
     */
    private void updateEntityFromDto(OpportunityUpdateDTO dto, Opportunity opp) {
        if (dto.title() != null) opp.setTitle(dto.title());
        if (dto.solicitationNumber() != null) opp.setSolicitationNumber(dto.solicitationNumber());
        if (dto.officeName() != null) opp.setOfficeName(dto.officeName());
        if (dto.noticeType() != null) opp.setNoticeType(dto.noticeType());
        if (dto.baseType() != null) opp.setBaseType(dto.baseType());
        if (dto.archiveType() != null) opp.setArchiveType(dto.archiveType());
        if (dto.archiveDate() != null) opp.setArchiveDate(dto.archiveDate());
        if (dto.naicsCode() != null) opp.setNaicsCode(dto.naicsCode());
        if (dto.classificationCode() != null) opp.setClassificationCode(dto.classificationCode());
        if (dto.setAside() != null) opp.setSetAside(dto.setAside());
        if (dto.postedDate() != null) opp.setPostedDate(dto.postedDate());
        if (dto.responseDeadline() != null) opp.setResponseDeadline(dto.responseDeadline());
        if (dto.description() != null) opp.setDescription(dto.description());
        if (dto.additionalInfoLink() != null) opp.setAdditionalInfoLink(dto.additionalInfoLink());
        if (dto.uiLink() != null) opp.setUiLink(dto.uiLink());
        if (dto.pointOfContact() != null) opp.setPointOfContact(dto.pointOfContact());
        if (dto.placeOfPerformanceCity() != null) opp.setPlaceOfPerformanceCity(dto.placeOfPerformanceCity());
        if (dto.placeOfPerformanceState() != null) opp.setPlaceOfPerformanceState(dto.placeOfPerformanceState());
        if (dto.placeOfPerformanceZip() != null) opp.setPlaceOfPerformanceZip(dto.placeOfPerformanceZip());
        if (dto.placeOfPerformanceCountry() != null) opp.setPlaceOfPerformanceCountry(dto.placeOfPerformanceCountry());
        if (dto.isActive() != null) opp.setIsActive(dto.isActive());
    }
}
