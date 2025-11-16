package com.athena.core.loader;

import com.athena.core.dto.SamGovOpportunityDto;
import com.athena.core.entity.*;
import com.athena.core.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * File-based data loader for SAM.gov cached JSON files.
 *
 * This loader reads static JSON files (from previous Cerberus system) and loads
 * them into the PostgreSQL database. No live SAM.gov API integration (no API key
 * available for prototype).
 *
 * Supports:
 * - Opportunity data (notices from SAM.gov)
 * - Award data (contract awards)
 * - Vendor data (contractor information)
 */
@Service
public class SamGovDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(SamGovDataLoader.class);

    private final ObjectMapper objectMapper;
    private final OpportunityRepository opportunityRepository;
    private final AgencyRepository agencyRepository;

    public SamGovDataLoader(
            OpportunityRepository opportunityRepository,
            AgencyRepository agencyRepository) {
        this.opportunityRepository = opportunityRepository;
        this.agencyRepository = agencyRepository;

        // Configure Jackson ObjectMapper with Java 8 date/time support
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Load opportunities from a JSON file.
     *
     * Expected JSON format: Array of opportunity objects matching SamGovOpportunityDto structure
     *
     * @param jsonFilePath Path to the JSON file containing opportunities
     * @return Number of opportunities loaded
     * @throws IOException if file reading or JSON parsing fails
     */
    @Transactional
    public int loadOpportunitiesFromFile(String jsonFilePath) throws IOException {
        logger.info("Loading opportunities from file: {}", jsonFilePath);

        File jsonFile = new File(jsonFilePath);
        if (!jsonFile.exists()) {
            throw new IOException("File not found: " + jsonFilePath);
        }

        // Parse JSON file into DTOs
        List<SamGovOpportunityDto> dtos = objectMapper.readValue(
            jsonFile,
            new TypeReference<List<SamGovOpportunityDto>>() {}
        );

        logger.info("Parsed {} opportunities from JSON", dtos.size());

        int loadedCount = 0;
        int skippedCount = 0;

        for (SamGovOpportunityDto dto : dtos) {
            try {
                // Check if opportunity already exists (by notice ID)
                String noticeId = dto.getNoticeId() != null ? dto.getNoticeId() : dto.getSolicitationNumber();

                if (noticeId != null && opportunityRepository.existsByNoticeId(noticeId)) {
                    logger.debug("Opportunity already exists: {}", noticeId);
                    skippedCount++;
                    continue;
                }

                // Convert DTO to entity and save
                Opportunity opportunity = convertToEntity(dto);
                opportunityRepository.save(opportunity);
                loadedCount++;

            } catch (Exception e) {
                logger.error("Failed to load opportunity: {}", dto.getSolicitationNumber(), e);
                skippedCount++;
            }
        }

        logger.info("Loaded {} opportunities, skipped {} (duplicates or errors)", loadedCount, skippedCount);
        return loadedCount;
    }

    /**
     * Convert SamGovOpportunityDto to Opportunity entity.
     *
     * Handles lookups for related entities (Agency) and creates them if they don't exist.
     * Note: Opportunity entity stores NAICS, SetAside, NoticeType as simple string fields,
     * not as relationships in the current schema.
     */
    private Opportunity convertToEntity(SamGovOpportunityDto dto) {
        Opportunity opportunity = new Opportunity();

        // Required fields
        opportunity.setNoticeId(dto.getNoticeId() != null ? dto.getNoticeId() : dto.getSolicitationNumber());
        opportunity.setTitle(dto.getTitle());
        opportunity.setNoticeType(dto.getNoticeType() != null ? dto.getNoticeType() : "Unknown");

        // Optional fields
        opportunity.setSolicitationNumber(dto.getSolicitationNumber());
        opportunity.setDescription(dto.getDescription());
        opportunity.setPostedDate(dto.getPostedDate());

        // Response deadline - DTO has LocalDate, entity expects Instant
        if (dto.getResponseDeadline() != null) {
            opportunity.setResponseDeadline(dto.getResponseDeadline().atStartOfDay()
                .atZone(java.time.ZoneId.of("UTC")).toInstant());
        }

        // Place of performance
        if (dto.getPlaceOfPerformance() != null) {
            var pop = dto.getPlaceOfPerformance();
            opportunity.setPlaceOfPerformanceCity(pop.getCity());
            opportunity.setPlaceOfPerformanceState(pop.getState());
            opportunity.setPlaceOfPerformanceCountry(
                pop.getCountry() != null ? pop.getCountry() : "US"
            );
            opportunity.setPlaceOfPerformanceZip(pop.getZip());
        }

        // Related entities - Agency
        if (dto.getDepartment() != null) {
            Agency agency = findOrCreateAgency(dto.getDepartment(), dto.getSubTier());
            opportunity.setAgency(agency);
        }

        // Office name
        if (dto.getOffice() != null) {
            opportunity.setOfficeName(dto.getOffice());
        }

        // Simple string fields (not relationships in current schema)
        opportunity.setNaicsCode(dto.getNaicsCode());
        opportunity.setSetAside(dto.getSetAside());
        opportunity.setClassificationCode(dto.getClassificationCode());

        // Archive metadata
        if ("Yes".equalsIgnoreCase(dto.getArchive())) {
            opportunity.setArchiveType("archived");
        }

        // Links
        opportunity.setUiLink(dto.getUiLink());
        opportunity.setAdditionalInfoLink(dto.getAdditionalInfoLink());

        // Active status
        opportunity.setIsActive("Yes".equalsIgnoreCase(dto.getActive()));

        return opportunity;
    }

    private Agency findOrCreateAgency(String departmentName, String subTier) {
        // Try to find by name using case-insensitive search
        List<Agency> matches = agencyRepository.findByNameContainingIgnoreCase(departmentName);

        if (!matches.isEmpty()) {
            // Return first match
            return matches.get(0);
        }

        // Create new agency
        Agency agency = new Agency();
        agency.setName(departmentName);
        agency.setAbbreviation(generateAbbreviation(departmentName));
        agency.setDepartment(departmentName);
        agency.setTier(subTier);
        agency.setIsActive(true);

        return agencyRepository.save(agency);
    }

    /**
     * Generate simple abbreviation from agency name.
     * Takes first letter of each word, max 10 characters.
     */
    private String generateAbbreviation(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }

        String[] words = name.split("\\s+");
        StringBuilder abbrev = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                abbrev.append(word.charAt(0));
            }
        }

        String result = abbrev.toString().toUpperCase();
        return result.length() > 10 ? result.substring(0, 10) : result;
    }
}
