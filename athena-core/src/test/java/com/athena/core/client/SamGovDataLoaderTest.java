package com.athena.core.client;

import com.athena.core.AbstractIntegrationTest;
import com.athena.core.entity.Opportunity;
import com.athena.core.repository.OpportunityRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.jdbc.Sql;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for SAM.gov JSON data loader.
 * Tests file-based data loading from cached JSON files into PostgreSQL database.
 *
 * <p>Tests cover:
 * <ul>
 *   <li>Valid JSON file parsing and entity creation</li>
 *   <li>Data persistence to database (via Testcontainers)</li>
 *   <li>Error handling for malformed JSON</li>
 *   <li>Error handling for missing required fields</li>
 *   <li>Error handling for missing files</li>
 * </ul>
 * </p>
 *
 * <p>Note: Per MEM-005, SAM.gov API client is NOT implemented for prototype.
 * This loader uses cached JSON files for proof-of-concept demonstration.</p>
 *
 * <p>Tests are disabled until SamGovDataLoader is implemented by Backend Architect.</p>
 */
@Disabled("Waiting for SamGovDataLoader implementation from Backend Architect")
public class SamGovDataLoaderTest extends AbstractIntegrationTest {

    // TODO: Inject SamGovDataLoader once implemented
    // @Autowired
    // private SamGovDataLoader dataLoader;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Test
    @Sql(statements = "DELETE FROM opportunities") // Clean state
    void testLoadValidOpportunityJson() throws IOException {
        // Given: Valid SAM.gov opportunity JSON file
        File jsonFile = new ClassPathResource("samgov-data/valid-opportunity.json").getFile();
        assertTrue(jsonFile.exists(), "Test data file should exist");

        // When: Load JSON file into database
        // TODO: Implement once SamGovDataLoader exists
        // dataLoader.loadOpportunityFile(jsonFile.getAbsolutePath());

        // Then: Verify opportunity persisted to database
        List<Opportunity> opportunities = opportunityRepository.findAll();
        // assertEquals(1, opportunities.size(), "Should persist one opportunity");

        // Opportunity opportunity = opportunities.get(0);
        // assertEquals("TEST-NOTICE-001", opportunity.getNoticeId());
        // assertEquals("IT Services for Federal Agency", opportunity.getTitle());
        // assertEquals("SOL-2025-001", opportunity.getSolicitationNumber());
        // assertEquals("Department of Defense", opportunity.getDepartment());
        // assertEquals("Army", opportunity.getSubTier());
        // assertEquals("541512", opportunity.getNaicsCode());
        // assertEquals("US", opportunity.getPlaceOfPerformanceCountry());
        // assertTrue(opportunity.isActive());
    }

    @Test
    void testLoadMalformedJson() throws IOException {
        // Given: Malformed JSON file (missing closing brace)
        File jsonFile = new ClassPathResource("samgov-data/malformed.json").getFile();

        // When/Then: Expect exception for malformed JSON
        // TODO: Implement once SamGovDataLoader exists
        // assertThrows(JsonParseException.class, () -> {
        //     dataLoader.loadOpportunityFile(jsonFile.getAbsolutePath());
        // }, "Should throw exception for malformed JSON");
    }

    @Test
    void testLoadInvalidOpportunityJson() throws IOException {
        // Given: JSON with missing required fields
        File jsonFile = new ClassPathResource("samgov-data/invalid-opportunity.json").getFile();

        // When/Then: Expect validation exception
        // TODO: Implement once SamGovDataLoader exists
        // assertThrows(ValidationException.class, () -> {
        //     dataLoader.loadOpportunityFile(jsonFile.getAbsolutePath());
        // }, "Should throw exception for missing required fields");
    }

    @Test
    void testLoadMissingFile() {
        // Given: Non-existent file path
        String nonExistentPath = "/tmp/does-not-exist.json";

        // When/Then: Expect exception for missing file
        // TODO: Implement once SamGovDataLoader exists
        // assertThrows(IOException.class, () -> {
        //     dataLoader.loadOpportunityFile(nonExistentPath);
        // }, "Should throw exception for missing file");
    }

    @Test
    @Sql(statements = "DELETE FROM opportunities") // Clean state
    void testLoadMultipleOpportunities() throws IOException {
        // Given: JSON file with array of opportunities
        // TODO: Create multi-opportunity test file
        // File jsonFile = new ClassPathResource("samgov-data/multiple-opportunities.json").getFile();

        // When: Load multiple opportunities
        // TODO: Implement once SamGovDataLoader exists
        // dataLoader.loadOpportunityFile(jsonFile.getAbsolutePath());

        // Then: Verify all opportunities persisted
        // List<Opportunity> opportunities = opportunityRepository.findAll();
        // assertEquals(5, opportunities.size(), "Should persist all 5 opportunities");
    }

    @Test
    @Sql(statements = "DELETE FROM opportunities") // Clean state
    void testLoadDirectoryOfJsonFiles() throws IOException {
        // Given: Directory containing multiple JSON files
        // TODO: Implement once SamGovDataLoader exists
        // File directory = new ClassPathResource("samgov-data").getFile();

        // When: Load all JSON files in directory
        // dataLoader.loadOpportunityDirectory(directory.getAbsolutePath());

        // Then: Verify all opportunities loaded
        // List<Opportunity> opportunities = opportunityRepository.findAll();
        // assertTrue(opportunities.size() >= 1, "Should load opportunities from all files");
    }

    @Test
    void testDuplicateOpportunityHandling() throws IOException {
        // Given: Same opportunity loaded twice (same noticeId)
        File jsonFile = new ClassPathResource("samgov-data/valid-opportunity.json").getFile();

        // When: Load same file twice
        // TODO: Implement once SamGovDataLoader exists
        // dataLoader.loadOpportunityFile(jsonFile.getAbsolutePath());

        // Then: Expect exception or update (depending on implementation strategy)
        // Option 1: Throw DuplicateEntityException
        // assertThrows(DuplicateEntityException.class, () -> {
        //     dataLoader.loadOpportunityFile(jsonFile.getAbsolutePath());
        // });

        // Option 2: Update existing opportunity
        // dataLoader.loadOpportunityFile(jsonFile.getAbsolutePath());
        // List<Opportunity> opportunities = opportunityRepository.findAll();
        // assertEquals(1, opportunities.size(), "Should not create duplicate");
    }

    @Test
    void testJsonFieldMapping() throws IOException {
        // Given: Valid JSON with all fields populated
        File jsonFile = new ClassPathResource("samgov-data/valid-opportunity.json").getFile();

        // When: Load JSON
        // TODO: Implement once SamGovDataLoader exists
        // dataLoader.loadOpportunityFile(jsonFile.getAbsolutePath());

        // Then: Verify all JSON fields mapped to entity fields correctly
        // Opportunity opportunity = opportunityRepository.findAll().get(0);

        // Core fields
        // assertEquals("TEST-NOTICE-001", opportunity.getNoticeId());
        // assertEquals("IT Services for Federal Agency", opportunity.getTitle());
        // assertEquals("SOL-2025-001", opportunity.getSolicitationNumber());

        // Agency fields
        // assertEquals("Department of Defense", opportunity.getDepartment());
        // assertEquals("Army", opportunity.getSubTier());
        // assertEquals("Army Contracting Command", opportunity.getOffice());

        // Dates
        // assertEquals(LocalDate.of(2025, 11, 1), opportunity.getPostedDate());
        // assertEquals(LocalDate.of(2025, 12, 15), opportunity.getResponseDeadLine());

        // Classification
        // assertEquals("541512", opportunity.getNaicsCode());
        // assertEquals("D", opportunity.getClassificationCode());

        // Contact info
        // assertEquals("John Smith", opportunity.getPointOfContact());
        // assertEquals("703-555-1234", opportunity.getPhoneNumber());
        // assertEquals("john.smith@army.mil", opportunity.getEmailAddress());

        // Place of performance
        // assertEquals("Washington", opportunity.getPlaceOfPerformanceCity());
        // assertEquals("DC", opportunity.getPlaceOfPerformanceState());
        // assertEquals("US", opportunity.getPlaceOfPerformanceCountry());
        // assertEquals("20001", opportunity.getPlaceOfPerformanceZip());

        // Set-aside
        // assertEquals("Total Small Business Set-Aside (FAR 19.5)", opportunity.getTypeOfSetAsideDescription());
        // assertEquals("SBA", opportunity.getTypeOfSetAside());

        // Status
        // assertTrue(opportunity.isActive());
    }
}
