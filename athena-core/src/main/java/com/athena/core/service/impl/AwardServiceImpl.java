package com.athena.core.service.impl;

import com.athena.core.dto.AwardCreateDTO;
import com.athena.core.dto.AwardResponseDTO;
import com.athena.core.dto.AwardUpdateDTO;
import com.athena.core.entity.Agency;
import com.athena.core.entity.Award;
import com.athena.core.entity.Opportunity;
import com.athena.core.entity.Organization;
import com.athena.core.exception.DuplicateEntityException;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.AgencyRepository;
import com.athena.core.repository.AwardRepository;
import com.athena.core.repository.OpportunityRepository;
import com.athena.core.repository.OrganizationRepository;
import com.athena.core.service.AwardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of AwardService.
 */
@Service
@Transactional(readOnly = true)
public class AwardServiceImpl implements AwardService {

    private final AwardRepository awardRepository;
    private final OpportunityRepository opportunityRepository;
    private final OrganizationRepository organizationRepository;
    private final AgencyRepository agencyRepository;

    public AwardServiceImpl(AwardRepository awardRepository,
                            OpportunityRepository opportunityRepository,
                            OrganizationRepository organizationRepository,
                            AgencyRepository agencyRepository) {
        this.awardRepository = awardRepository;
        this.opportunityRepository = opportunityRepository;
        this.organizationRepository = organizationRepository;
        this.agencyRepository = agencyRepository;
    }

    @Override
    @Transactional
    public AwardResponseDTO create(AwardCreateDTO dto) {
        // Check for duplicate contract number
        if (awardRepository.findByContractNumber(dto.contractNumber()).isPresent()) {
            throw new DuplicateEntityException("Award", "contractNumber", dto.contractNumber());
        }

        // Create award entity
        Award award = new Award(dto.contractNumber());
        award.setTitle(dto.title());
        award.setAwardeeName(dto.awardeeName());
        award.setAwardeeUei(dto.awardeeUei());
        award.setAwardeeDuns(dto.awardeeDuns());
        award.setAwardDate(dto.awardDate());
        award.setAwardAmount(dto.awardAmount());
        award.setCurrency(dto.currency());
        award.setStartDate(dto.startDate());
        award.setEndDate(dto.endDate());
        award.setAwardingOffice(dto.awardingOffice());
        award.setAwardType(dto.awardType());
        award.setNaicsCode(dto.naicsCode());
        award.setSetAside(dto.setAside());
        award.setDescription(dto.description());

        // Set optional relationships
        if (dto.opportunityId() != null) {
            Opportunity opportunity = opportunityRepository.findById(dto.opportunityId())
                .orElseThrow(() -> new EntityNotFoundException("Opportunity", dto.opportunityId()));
            award.setOpportunity(opportunity);
        }

        if (dto.organizationId() != null) {
            Organization organization = organizationRepository.findById(dto.organizationId())
                .orElseThrow(() -> new EntityNotFoundException("Organization", dto.organizationId()));
            award.setOrganization(organization);
        }

        if (dto.agencyId() != null) {
            Agency agency = agencyRepository.findById(dto.agencyId())
                .orElseThrow(() -> new EntityNotFoundException("Agency", dto.agencyId()));
            award.setAgency(agency);
        }

        // Save and return
        Award savedAward = awardRepository.save(award);
        return AwardResponseDTO.fromEntity(savedAward);
    }

    @Override
    public Optional<AwardResponseDTO> findById(UUID id) {
        return awardRepository.findById(id)
            .map(AwardResponseDTO::fromEntity);
    }

    @Override
    public Page<AwardResponseDTO> findAll(Pageable pageable) {
        return awardRepository.findAll(pageable)
            .map(AwardResponseDTO::fromEntity);
    }

    @Override
    @Transactional
    public AwardResponseDTO update(UUID id, AwardUpdateDTO dto) {
        Award award = awardRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Award", id));

        // Update fields if provided
        if (dto.title() != null) {
            award.setTitle(dto.title());
        }

        if (dto.awardeeName() != null) {
            award.setAwardeeName(dto.awardeeName());
        }

        if (dto.awardeeUei() != null) {
            award.setAwardeeUei(dto.awardeeUei());
        }

        if (dto.awardeeDuns() != null) {
            award.setAwardeeDuns(dto.awardeeDuns());
        }

        if (dto.awardDate() != null) {
            award.setAwardDate(dto.awardDate());
        }

        if (dto.awardAmount() != null) {
            award.setAwardAmount(dto.awardAmount());
        }

        if (dto.currency() != null) {
            award.setCurrency(dto.currency());
        }

        if (dto.startDate() != null) {
            award.setStartDate(dto.startDate());
        }

        if (dto.endDate() != null) {
            award.setEndDate(dto.endDate());
        }

        if (dto.awardingOffice() != null) {
            award.setAwardingOffice(dto.awardingOffice());
        }

        if (dto.awardType() != null) {
            award.setAwardType(dto.awardType());
        }

        if (dto.naicsCode() != null) {
            award.setNaicsCode(dto.naicsCode());
        }

        if (dto.setAside() != null) {
            award.setSetAside(dto.setAside());
        }

        if (dto.description() != null) {
            award.setDescription(dto.description());
        }

        if (dto.isActive() != null) {
            award.setIsActive(dto.isActive());
        }

        // Update optional relationships
        if (dto.organizationId() != null) {
            Organization organization = organizationRepository.findById(dto.organizationId())
                .orElseThrow(() -> new EntityNotFoundException("Organization", dto.organizationId()));
            award.setOrganization(organization);
        }

        if (dto.agencyId() != null) {
            Agency agency = agencyRepository.findById(dto.agencyId())
                .orElseThrow(() -> new EntityNotFoundException("Agency", dto.agencyId()));
            award.setAgency(agency);
        }

        Award updatedAward = awardRepository.save(award);
        return AwardResponseDTO.fromEntity(updatedAward);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Award award = awardRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Award", id));

        // Soft delete
        award.setIsActive(false);
        awardRepository.save(award);
    }

    @Override
    public Optional<AwardResponseDTO> findByContractNumber(String contractNumber) {
        return awardRepository.findByContractNumber(contractNumber)
            .map(AwardResponseDTO::fromEntity);
    }

    @Override
    public List<AwardResponseDTO> findActiveAwards() {
        return awardRepository.findByIsActiveTrue()
            .stream()
            .map(AwardResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<AwardResponseDTO> findByAwardDateBetween(LocalDate startDate, LocalDate endDate) {
        return awardRepository.findByAwardDateBetween(startDate, endDate)
            .stream()
            .map(AwardResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<AwardResponseDTO> findByNaicsCode(String naicsCode) {
        return awardRepository.findByNaicsCode(naicsCode)
            .stream()
            .map(AwardResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<AwardResponseDTO> findByAwardeeUei(String awardeeUei) {
        return awardRepository.findByAwardeeUei(awardeeUei)
            .stream()
            .map(AwardResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public boolean existsByContractNumber(String contractNumber) {
        return awardRepository.findByContractNumber(contractNumber).isPresent();
    }
}
