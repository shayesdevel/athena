package com.athena.core.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MicrosoftTeamsClient.
 * Uses MockWebServer to simulate Teams webhook responses.
 */
class MicrosoftTeamsClientTest {

    private MockWebServer mockWebServer;
    private MicrosoftTeamsClient client;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        objectMapper = new ObjectMapper();

        // Create client pointing to mock webhook
        String webhookUrl = mockWebServer.url("/webhook").toString();
        client = new MicrosoftTeamsClient(webhookUrl, true, objectMapper);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testSendMessage_success() throws InterruptedException {
        // Mock Teams webhook success response
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .setBody("1"));

        // Execute
        client.sendMessage("Test Title", "Test message body");

        // Verify request
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/webhook", request.getPath());
        assertEquals("POST", request.getMethod());

        // Verify JSON body contains expected fields
        String body = request.getBody().readUtf8();
        assertTrue(body.contains("Test Title"));
        assertTrue(body.contains("Test message body"));
        assertTrue(body.contains("@type"));
        assertTrue(body.contains("MessageCard"));
    }

    @Test
    void testSendHighScoreOpportunityAlert_success() throws InterruptedException {
        // Mock successful response
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("1"));

        // Execute
        client.sendHighScoreOpportunityAlert(
            "Cloud Services Opportunity",
            92,
            "SOL-2025-001",
            "2025-03-15",
            "https://sam.gov/opportunity/123"
        );

        // Verify request
        RecordedRequest request = mockWebServer.takeRequest();
        String body = request.getBody().readUtf8();

        // Verify content
        assertTrue(body.contains("High-Score Opportunity"));
        assertTrue(body.contains("Cloud Services Opportunity"));
        assertTrue(body.contains("92/100"));
        assertTrue(body.contains("SOL-2025-001"));
        assertTrue(body.contains("2025-03-15"));
        assertTrue(body.contains("28a745")); // Green theme color
    }

    @Test
    void testSendCaptureTeamAlert_success() throws InterruptedException {
        // Mock successful response
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("1"));

        // Execute
        client.sendCaptureTeamAlert(
            "Alpha Team",
            "IT Infrastructure",
            "Proposal deadline approaching in 7 days"
        );

        // Verify request
        RecordedRequest request = mockWebServer.takeRequest();
        String body = request.getBody().readUtf8();

        // Verify content
        assertTrue(body.contains("Capture Team Alert"));
        assertTrue(body.contains("Alpha Team"));
        assertTrue(body.contains("IT Infrastructure"));
        assertTrue(body.contains("Proposal deadline approaching"));
        assertTrue(body.contains("ffc107")); // Amber theme color
    }

    @Test
    void testSendSystemNotification_success() throws InterruptedException {
        // Mock successful response
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("1"));

        // Execute
        client.sendSystemNotification(
            "Sync Completed",
            "Successfully synced 150 opportunities",
            false
        );

        // Verify request
        RecordedRequest request = mockWebServer.takeRequest();
        String body = request.getBody().readUtf8();

        // Verify content
        assertTrue(body.contains("Sync Completed"));
        assertTrue(body.contains("Successfully synced 150 opportunities"));
        assertTrue(body.contains("28a745")); // Green for success
    }

    @Test
    void testSendSystemNotification_error() throws InterruptedException {
        // Mock successful response
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("1"));

        // Execute with error flag
        client.sendSystemNotification(
            "Sync Failed",
            "Connection timeout to SAM.gov API",
            true
        );

        // Verify request
        RecordedRequest request = mockWebServer.takeRequest();
        String body = request.getBody().readUtf8();

        // Verify content
        assertTrue(body.contains("Sync Failed"));
        assertTrue(body.contains("Connection timeout"));
        assertTrue(body.contains("dc3545")); // Red for error
    }

    @Test
    void testSendMessage_webhookError() {
        // Mock webhook error (should not throw exception - just log)
        mockWebServer.enqueue(new MockResponse().setResponseCode(400).setBody("Bad Request"));

        // Execute - should not throw exception
        assertDoesNotThrow(() -> {
            client.sendMessage("Test", "This should fail gracefully");
        });
    }

    @Test
    void testSendMessage_disabledClient() {
        // Create disabled client (empty webhook URL)
        MicrosoftTeamsClient disabledClient = new MicrosoftTeamsClient("", false, objectMapper);

        // Should not send any requests
        assertDoesNotThrow(() -> {
            disabledClient.sendMessage("Test", "Should be skipped");
        });

        // Verify no requests made
        assertEquals(0, mockWebServer.getRequestCount());
    }

    @Test
    void testSendMessage_noWebhookUrl() {
        // Create client with enabled=true but no URL
        MicrosoftTeamsClient noUrlClient = new MicrosoftTeamsClient("", true, objectMapper);

        // Should not crash - just skip
        assertDoesNotThrow(() -> {
            noUrlClient.sendMessage("Test", "Should be skipped");
        });

        // Verify no requests made
        assertEquals(0, mockWebServer.getRequestCount());
    }
}
