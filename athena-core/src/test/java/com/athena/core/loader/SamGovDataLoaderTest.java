package com.athena.core.loader;

import com.athena.core.entity.Agency;
import com.athena.core.entity.Opportunity;
import com.athena.core.repository.AgencyRepository;
import com.athena.core.repository.OpportunityRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SamGovDataLoader.
 * Uses mocked repositories to test JSON parsing and entity conversion logic.
 */
class SamGovDataLoaderTest {

    private SamGovDataLoader dataLoader;
    private OpportunityRepository opportunityRepository;
    private AgencyRepository agencyRepository;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        opportunityRepository = mock(OpportunityRepository.class);
        agencyRepository = mock(AgencyRepository.class);
        dataLoader = new SamGovDataLoader(opportunityRepository, agencyRepository);
    }

    @Test
    void testLoadOpportunitiesFromFile_success() throws IOException {
        // Create test JSON file
        String jsonContent = "[{" +
            "\"noticeId\": \"test-001\"," +
            "\"solicitationNumber\": \"SOL-2025-001\"," +
            "\"title\": \"Test Opportunity\"," +
            "\"description\": \"Test description\"," +
            "\"department\": \"Department of Defense\"," +
            "\"subTier\": \"Army\"," +
            "\"office\": \"CECOM\"," +
            "\"postedDate\": \"2025-01-15\"," +
            "\"responseDeadLine\": \"2025-02-15\"," +
            "\"naicsCode\": \"541512\"," +
            "\"setAside\": \"8A\"," +
            "\"type\": \"Presolicitation\"," +
            "\"active\": \"Yes\"," +
            "\"archive\": \"No\"" +
            "}]";

        Path jsonFile = tempDir.resolve("opportunities.json");
        Files.writeString(jsonFile, jsonContent);

        // Mock repository behavior
        when(opportunityRepository.existsByNoticeId(anyString())).thenReturn(false);
        when(agencyRepository.findByNameContainingIgnoreCase(anyString())).thenReturn(Collections.emptyList());
        when(agencyRepository.save(any(Agency.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(opportunityRepository.save(any(Opportunity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Execute
        int count = dataLoader.loadOpportunitiesFromFile(jsonFile.toString());

        // Verify
        assertEquals(1, count);
        verify(opportunityRepository, times(1)).save(any(Opportunity.class));
        verify(agencyRepository, times(1)).save(any(Agency.class));
    }

    @Test
    void testLoadOpportunitiesFromFile_skipsDuplicates() throws IOException {
        // Create test JSON file with 2 opportunities
        String jsonContent = "[" +
            "{\"noticeId\": \"test-001\", \"title\": \"Opportunity 1\", \"type\": \"Presolicitation\"}," +
            "{\"noticeId\": \"test-002\", \"title\": \"Opportunity 2\", \"type\": \"Presolicitation\"}" +
            "]";

        Path jsonFile = tempDir.resolve("opportunities.json");
        Files.writeString(jsonFile, jsonContent);

        // Mock: first exists, second doesn't
        when(opportunityRepository.existsByNoticeId("test-001")).thenReturn(true);
        when(opportunityRepository.existsByNoticeId("test-002")).thenReturn(false);
        when(opportunityRepository.save(any(Opportunity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Execute
        int count = dataLoader.loadOpportunitiesFromFile(jsonFile.toString());

        // Verify - only 1 saved (the non-duplicate)
        assertEquals(1, count);
        verify(opportunityRepository, times(1)).save(any(Opportunity.class));
    }

    @Test
    void testLoadOpportunitiesFromFile_fileNotFound() {
        // Execute and verify exception
        IOException exception = assertThrows(IOException.class, () -> {
            dataLoader.loadOpportunitiesFromFile("/nonexistent/file.json");
        });

        assertTrue(exception.getMessage().contains("File not found"));
    }

    @Test
    void testLoadOpportunitiesFromFile_invalidJson() throws IOException {
        // Create invalid JSON file
        Path jsonFile = tempDir.resolve("invalid.json");
        Files.writeString(jsonFile, "{invalid json");

        // Execute and verify exception
        assertThrows(IOException.class, () -> {
            dataLoader.loadOpportunitiesFromFile(jsonFile.toString());
        });
    }

    @Test
    void testLoadOpportunitiesFromFile_createsAgencyIfNotExists() throws IOException {
        // Create test JSON
        String jsonContent = "[{" +
            "\"noticeId\": \"test-001\"," +
            "\"title\": \"Test Opportunity\"," +
            "\"department\": \"Department of Defense\"," +
            "\"subTier\": \"Army\"," +
            "\"type\": \"Presolicitation\"" +
            "}]";

        Path jsonFile = tempDir.resolve("opportunities.json");
        Files.writeString(jsonFile, jsonContent);

        // Mock: agency doesn't exist
        when(opportunityRepository.existsByNoticeId(anyString())).thenReturn(false);
        when(agencyRepository.findByNameContainingIgnoreCase(anyString())).thenReturn(Collections.emptyList());
        when(agencyRepository.save(any(Agency.class))).thenAnswer(invocation -> {
            Agency agency = invocation.getArgument(0);
            assertEquals("Department of Defense", agency.getName());
            assertEquals("Army", agency.getTier());
            assertNotNull(agency.getAbbreviation());
            return agency;
        });
        when(opportunityRepository.save(any(Opportunity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Execute
        int count = dataLoader.loadOpportunitiesFromFile(jsonFile.toString());

        // Verify
        assertEquals(1, count);
        verify(agencyRepository, times(1)).save(any(Agency.class));
    }

    @Test
    void testLoadOpportunitiesFromFile_reusesExistingAgency() throws IOException {
        // Create test JSON
        String jsonContent = "[{" +
            "\"noticeId\": \"test-001\"," +
            "\"title\": \"Test Opportunity\"," +
            "\"department\": \"Department of Defense\"," +
            "\"type\": \"Presolicitation\"" +
            "}]";

        Path jsonFile = tempDir.resolve("opportunities.json");
        Files.writeString(jsonFile, jsonContent);

        // Mock: agency exists
        Agency existingAgency = new Agency();
        existingAgency.setName("Department of Defense");

        when(opportunityRepository.existsByNoticeId(anyString())).thenReturn(false);
        when(agencyRepository.findByNameContainingIgnoreCase(anyString())).thenReturn(List.of(existingAgency));
        when(opportunityRepository.save(any(Opportunity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Execute
        int count = dataLoader.loadOpportunitiesFromFile(jsonFile.toString());

        // Verify - agency not saved (reused)
        assertEquals(1, count);
        verify(agencyRepository, never()).save(any(Agency.class));
    }
}
