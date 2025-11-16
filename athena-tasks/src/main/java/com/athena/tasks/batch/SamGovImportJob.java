package com.athena.tasks.batch;

import com.athena.core.dto.SamGovOpportunityDto;
import com.athena.core.entity.Agency;
import com.athena.core.entity.Opportunity;
import com.athena.core.repository.AgencyRepository;
import com.athena.core.repository.OpportunityRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Spring Batch job for importing SAM.gov opportunity data from JSON files.
 *
 * Job: samGovImportJob
 * Step: importOpportunitiesStep
 *
 * Processing flow:
 * 1. Reader: Read JSON files from configured data directory
 * 2. Processor: Convert DTO to entity, validate, check for duplicates
 * 3. Writer: Bulk insert to database via OpportunityRepository
 *
 * Configuration:
 * - athena.data.loader.sam-gov.data-directory: Directory containing JSON files
 * - Chunk size: 50 (batch inserts of 50 opportunities at a time)
 * - Skip policy: Skip individual failures, continue processing
 */
@Configuration
public class SamGovImportJob {

    private static final Logger logger = LoggerFactory.getLogger(SamGovImportJob.class);

    private final OpportunityRepository opportunityRepository;
    private final AgencyRepository agencyRepository;
    private final ObjectMapper objectMapper;

    @Value("${athena.data.loader.sam-gov.data-directory:./data/sam-gov}")
    private String dataDirectory;

    public SamGovImportJob(
            OpportunityRepository opportunityRepository,
            AgencyRepository agencyRepository) {
        this.opportunityRepository = opportunityRepository;
        this.agencyRepository = agencyRepository;

        // Configure ObjectMapper with Java 8 date/time support
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Define the SAM.gov import job.
     *
     * @param jobRepository Spring Batch job repository
     * @param importOpportunitiesStep Step for importing opportunities
     * @return Configured job
     */
    @Bean
    public Job samGovImportJob(
            JobRepository jobRepository,
            Step importOpportunitiesStep) {
        return new JobBuilder("samGovImportJob", jobRepository)
                .start(importOpportunitiesStep)
                .build();
    }

    /**
     * Define the import opportunities step.
     *
     * @param jobRepository Spring Batch job repository
     * @param transactionManager Transaction manager
     * @return Configured step
     */
    @Bean
    public Step importOpportunitiesStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager) {
        return new StepBuilder("importOpportunitiesStep", jobRepository)
                .<SamGovOpportunityDto, Opportunity>chunk(50, transactionManager)
                .reader(opportunityReader())
                .processor(opportunityProcessor())
                .writer(opportunityWriter())
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(Integer.MAX_VALUE)
                .build();
    }

    /**
     * Reader: Read SAM.gov opportunity DTOs from JSON files.
     *
     * Scans configured data directory for *.json files and parses them.
     * Returns ListItemReader containing all opportunities from all files.
     *
     * @return Item reader
     */
    @Bean
    public ItemReader<SamGovOpportunityDto> opportunityReader() {
        List<SamGovOpportunityDto> allOpportunities = new ArrayList<>();

        Path dataPath = Paths.get(dataDirectory);
        if (!Files.exists(dataPath)) {
            logger.warn("Data directory does not exist: {}", dataDirectory);
            return new ListItemReader<>(allOpportunities);
        }

        // Find all JSON files in data directory
        try (Stream<Path> paths = Files.walk(dataPath)) {
            List<Path> jsonFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .toList();

            logger.info("Found {} JSON files in {}", jsonFiles.size(), dataDirectory);

            for (Path jsonFile : jsonFiles) {
                try {
                    List<SamGovOpportunityDto> opportunities = objectMapper.readValue(
                            jsonFile.toFile(),
                            new TypeReference<List<SamGovOpportunityDto>>() {}
                    );
                    allOpportunities.addAll(opportunities);
                    logger.info("Loaded {} opportunities from {}", opportunities.size(), jsonFile.getFileName());
                } catch (IOException e) {
                    logger.error("Failed to parse JSON file: {}", jsonFile, e);
                }
            }

            logger.info("Total opportunities to process: {}", allOpportunities.size());

        } catch (IOException e) {
            logger.error("Failed to scan data directory", e);
        }

        return new ListItemReader<>(allOpportunities);
    }

    /**
     * Processor: Convert SamGovOpportunityDto to Opportunity entity.
     *
     * Validates data, checks for duplicates (by notice ID), and creates related entities (Agency).
     * Returns null for duplicates to skip them.
     *
     * @return Item processor
     */
    @Bean
    public ItemProcessor<SamGovOpportunityDto, Opportunity> opportunityProcessor() {
        return dto -> {
            try {
                // Check for duplicate by notice ID
                String noticeId = dto.getNoticeId() != null ? dto.getNoticeId() : dto.getSolicitationNumber();
                if (noticeId != null && opportunityRepository.existsByNoticeId(noticeId)) {
                    logger.debug("Skipping duplicate opportunity: {}", noticeId);
                    return null; // Skip this item
                }

                // Convert DTO to entity
                Opportunity opportunity = convertToEntity(dto);
                return opportunity;

            } catch (Exception e) {
                logger.error("Failed to process opportunity: {}", dto.getSolicitationNumber(), e);
                return null; // Skip this item
            }
        };
    }

    /**
     * Writer: Bulk insert opportunities to database.
     *
     * Uses OpportunityRepository.saveAll() for batch inserts.
     *
     * @return Item writer
     */
    @Bean
    public ItemWriter<Opportunity> opportunityWriter() {
        return chunk -> {
            // Filter out nulls (duplicates/errors from processor)
            List<Opportunity> opportunities = chunk.getItems().stream()
                    .map(item -> (Opportunity) item)
                    .filter(opp -> opp != null)
                    .toList();

            if (!opportunities.isEmpty()) {
                opportunityRepository.saveAll(opportunities);
                logger.info("Saved {} opportunities to database", opportunities.size());
            }
        };
    }

    /**
     * Convert SamGovOpportunityDto to Opportunity entity.
     *
     * Handles lookups for related entities (Agency) and creates them if they don't exist.
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

        // Simple string fields
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
        // Try to find by name
        List<Agency> matches = agencyRepository.findByNameContainingIgnoreCase(departmentName);

        if (!matches.isEmpty()) {
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
