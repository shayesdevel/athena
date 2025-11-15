package com.athena.core.service;

import com.athena.core.dto.OrganizationCreateDTO;
import com.athena.core.dto.OrganizationResponseDTO;
import com.athena.core.dto.OrganizationUpdateDTO;
import com.athena.core.entity.Organization;
import com.athena.core.exception.DuplicateEntityException;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.OrganizationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of OrganizationService.
 */
@Service
@Transactional(readOnly = true)
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;

    public OrganizationServiceImpl(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Override
    @Transactional
    public OrganizationResponseDTO create(OrganizationCreateDTO dto) {
        // Check for duplicate UEI if provided
        if (dto.uei() != null && organizationRepository.existsByUei(dto.uei())) {
            throw new DuplicateEntityException("Organization", "UEI", dto.uei());
        }

        // Create organization entity
        Organization org = new Organization(dto.name());
        mapDtoToEntity(dto, org);

        // Save and return
        Organization savedOrg = organizationRepository.save(org);
        return OrganizationResponseDTO.fromEntity(savedOrg);
    }

    @Override
    public Optional<OrganizationResponseDTO> findById(UUID id) {
        return organizationRepository.findById(id)
            .map(OrganizationResponseDTO::fromEntity);
    }

    @Override
    public Page<OrganizationResponseDTO> findAll(Pageable pageable) {
        return organizationRepository.findAll(pageable)
            .map(OrganizationResponseDTO::fromEntity);
    }

    @Override
    @Transactional
    public OrganizationResponseDTO update(UUID id, OrganizationUpdateDTO dto) {
        Organization org = organizationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Organization", id));

        // Check for UEI conflict if changing UEI
        if (dto.uei() != null && !dto.uei().equals(org.getUei())) {
            if (organizationRepository.existsByUei(dto.uei())) {
                throw new DuplicateEntityException("Organization", "UEI", dto.uei());
            }
        }

        // Update fields if provided
        updateEntityFromDto(dto, org);

        Organization updatedOrg = organizationRepository.save(org);
        return OrganizationResponseDTO.fromEntity(updatedOrg);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!organizationRepository.existsById(id)) {
            throw new EntityNotFoundException("Organization", id);
        }
        organizationRepository.deleteById(id);
    }

    @Override
    public Optional<OrganizationResponseDTO> findByUei(String uei) {
        return organizationRepository.findByUei(uei)
            .map(OrganizationResponseDTO::fromEntity);
    }

    @Override
    public Optional<OrganizationResponseDTO> findByCageCode(String cageCode) {
        return organizationRepository.findByCageCode(cageCode)
            .map(OrganizationResponseDTO::fromEntity);
    }

    @Override
    public List<OrganizationResponseDTO> searchByName(String name) {
        return organizationRepository.findByNameContainingIgnoreCase(name)
            .stream()
            .map(OrganizationResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<OrganizationResponseDTO> findByPrimaryNaics(String primaryNaics) {
        return organizationRepository.findByPrimaryNaics(primaryNaics)
            .stream()
            .map(OrganizationResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<OrganizationResponseDTO> findSmallBusinesses() {
        return organizationRepository.findByIsSmallBusinessTrue()
            .stream()
            .map(OrganizationResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public boolean existsByUei(String uei) {
        return organizationRepository.existsByUei(uei);
    }

    /**
     * Map create DTO fields to entity.
     */
    private void mapDtoToEntity(OrganizationCreateDTO dto, Organization org) {
        org.setUei(dto.uei());
        org.setCageCode(dto.cageCode());
        org.setDuns(dto.duns());
        org.setSamUrl(dto.samUrl());
        org.setPrimaryNaics(dto.primaryNaics());
        org.setBusinessType(dto.businessType());
        org.setIsSmallBusiness(dto.isSmallBusiness() != null ? dto.isSmallBusiness() : false);
        org.setIsWomanOwned(dto.isWomanOwned() != null ? dto.isWomanOwned() : false);
        org.setIsVeteranOwned(dto.isVeteranOwned() != null ? dto.isVeteranOwned() : false);
        org.setIs8aCertified(dto.is8aCertified() != null ? dto.is8aCertified() : false);
        org.setStreetAddress(dto.streetAddress());
        org.setCity(dto.city());
        org.setStateCode(dto.stateCode());
        org.setZipCode(dto.zipCode());
        org.setCountryCode(dto.countryCode() != null ? dto.countryCode() : "US");
        org.setWebsiteUrl(dto.websiteUrl());
        org.setPhone(dto.phone());
    }

    /**
     * Update entity fields from update DTO (only non-null fields).
     */
    private void updateEntityFromDto(OrganizationUpdateDTO dto, Organization org) {
        if (dto.name() != null) org.setName(dto.name());
        if (dto.uei() != null) org.setUei(dto.uei());
        if (dto.cageCode() != null) org.setCageCode(dto.cageCode());
        if (dto.duns() != null) org.setDuns(dto.duns());
        if (dto.samUrl() != null) org.setSamUrl(dto.samUrl());
        if (dto.primaryNaics() != null) org.setPrimaryNaics(dto.primaryNaics());
        if (dto.businessType() != null) org.setBusinessType(dto.businessType());
        if (dto.isSmallBusiness() != null) org.setIsSmallBusiness(dto.isSmallBusiness());
        if (dto.isWomanOwned() != null) org.setIsWomanOwned(dto.isWomanOwned());
        if (dto.isVeteranOwned() != null) org.setIsVeteranOwned(dto.isVeteranOwned());
        if (dto.is8aCertified() != null) org.setIs8aCertified(dto.is8aCertified());
        if (dto.streetAddress() != null) org.setStreetAddress(dto.streetAddress());
        if (dto.city() != null) org.setCity(dto.city());
        if (dto.stateCode() != null) org.setStateCode(dto.stateCode());
        if (dto.zipCode() != null) org.setZipCode(dto.zipCode());
        if (dto.countryCode() != null) org.setCountryCode(dto.countryCode());
        if (dto.websiteUrl() != null) org.setWebsiteUrl(dto.websiteUrl());
        if (dto.phone() != null) org.setPhone(dto.phone());
    }
}
