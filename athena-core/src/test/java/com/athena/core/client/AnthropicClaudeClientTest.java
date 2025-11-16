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
 * Unit tests for AnthropicClaudeClient.
 * Uses MockWebServer to simulate Claude API responses.
 */
class AnthropicClaudeClientTest {

    private MockWebServer mockWebServer;
    private AnthropicClaudeClient client;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        objectMapper = new ObjectMapper();

        // Create client pointing to mock server
        String baseUrl = mockWebServer.url("/").toString();
        client = new AnthropicClaudeClient(
            "test-api-key",
            baseUrl,
            "claude-3-5-sonnet-20241022",
            objectMapper
        );
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testSendMessage_success() throws InterruptedException {
        // Mock successful response
        String mockResponse = "{\n" +
            "  \"id\": \"msg_123\",\n" +
            "  \"type\": \"message\",\n" +
            "  \"role\": \"assistant\",\n" +
            "  \"content\": [{\"type\": \"text\", \"text\": \"This is a test response\"}],\n" +
            "  \"model\": \"claude-3-5-sonnet-20241022\",\n" +
            "  \"stop_reason\": \"end_turn\",\n" +
            "  \"usage\": {\"input_tokens\": 10, \"output_tokens\": 20}\n" +
            "}";

        mockWebServer.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setHeader("Content-Type", "application/json"));

        // Execute
        String response = client.sendMessage("You are a helpful assistant", "Hello!", 100);

        // Verify response
        assertEquals("This is a test response", response);

        // Verify request
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/v1/messages", request.getPath());
        assertEquals("POST", request.getMethod());
        assertTrue(request.getHeader("x-api-key").equals("test-api-key"));
        assertTrue(request.getHeader("anthropic-version").equals("2023-06-01"));
    }

    @Test
    void testScoreOpportunity_success() throws InterruptedException {
        // Mock Claude response with score format
        String mockResponse = "{\n" +
            "  \"id\": \"msg_456\",\n" +
            "  \"type\": \"message\",\n" +
            "  \"role\": \"assistant\",\n" +
            "  \"content\": [{\"type\": \"text\", \"text\": \"SCORE: 85\\nRATIONALE: Good fit\"}],\n" +
            "  \"model\": \"claude-3-5-sonnet-20241022\",\n" +
            "  \"stop_reason\": \"end_turn\",\n" +
            "  \"usage\": {\"input_tokens\": 50, \"output_tokens\": 30}\n" +
            "}";

        mockWebServer.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setHeader("Content-Type", "application/json"));

        // Execute
        AnthropicClaudeClient.OpportunityScoreResult result = client.scoreOpportunity(
            "Cloud Infrastructure Services",
            "Provide AWS cloud infrastructure",
            "Expert in AWS services"
        );

        // Verify
        assertEquals(85, result.getScore());
        assertEquals("Good fit", result.getRationale());
    }

    @Test
    void testGenerateCaptureStrategy_success() throws InterruptedException {
        // Mock strategy response
        String mockResponse = "{\n" +
            "  \"id\": \"msg_789\",\n" +
            "  \"type\": \"message\",\n" +
            "  \"role\": \"assistant\",\n" +
            "  \"content\": [{\"type\": \"text\", \"text\": \"Win Theme 1: Technical excellence...\"}],\n" +
            "  \"model\": \"claude-3-5-sonnet-20241022\",\n" +
            "  \"stop_reason\": \"end_turn\",\n" +
            "  \"usage\": {\"input_tokens\": 100, \"output_tokens\": 200}\n" +
            "}";

        mockWebServer.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setHeader("Content-Type", "application/json"));

        // Execute
        String strategy = client.generateCaptureStrategy(
            "IT Support Services",
            "Provide helpdesk and IT support",
            "24/7 support capability"
        );

        // Verify
        assertTrue(strategy.contains("Win Theme 1"));
        assertTrue(strategy.contains("Technical excellence"));
    }

    @Test
    void testAnalyzeCompetitors_success() throws InterruptedException {
        // Mock competitive analysis response
        String mockResponse = "{\n" +
            "  \"id\": \"msg_abc\",\n" +
            "  \"type\": \"message\",\n" +
            "  \"role\": \"assistant\",\n" +
            "  \"content\": [{\"type\": \"text\", \"text\": \"Competitor Analysis: Strong in X...\"}],\n" +
            "  \"model\": \"claude-3-5-sonnet-20241022\",\n" +
            "  \"stop_reason\": \"end_turn\",\n" +
            "  \"usage\": {\"input_tokens\": 80, \"output_tokens\": 150}\n" +
            "}";

        mockWebServer.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setHeader("Content-Type", "application/json"));

        // Execute
        String analysis = client.analyzeCompetitors(
            "Cybersecurity Services",
            "Competitor A: CMMI Level 3, Competitor B: ISO 27001"
        );

        // Verify
        assertTrue(analysis.contains("Competitor Analysis"));
        assertTrue(analysis.contains("Strong in X"));
    }

    @Test
    void testSendMessage_apiError() {
        // Mock error response
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(429)
            .setBody("{\"error\": \"rate_limit_exceeded\"}"));

        // Execute and verify exception
        assertThrows(AnthropicClaudeClient.ClaudeApiException.class, () -> {
            client.sendMessage("System prompt", "User message", 100);
        });
    }

    @Test
    void testSendMessage_emptyResponse() {
        // Mock response with empty content
        String mockResponse = "{\n" +
            "  \"id\": \"msg_empty\",\n" +
            "  \"type\": \"message\",\n" +
            "  \"role\": \"assistant\",\n" +
            "  \"content\": [],\n" +
            "  \"model\": \"claude-3-5-sonnet-20241022\",\n" +
            "  \"stop_reason\": \"end_turn\",\n" +
            "  \"usage\": {\"input_tokens\": 10, \"output_tokens\": 0}\n" +
            "}";

        mockWebServer.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setHeader("Content-Type", "application/json"));

        // Execute and verify exception
        assertThrows(AnthropicClaudeClient.ClaudeApiException.class, () -> {
            client.sendMessage("System", "Message", 100);
        });
    }
}
