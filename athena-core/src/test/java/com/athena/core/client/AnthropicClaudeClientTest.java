package com.athena.core.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Anthropic Claude API client.
 * Uses WireMock to mock Claude API responses.
 *
 * <p>Tests cover:
 * <ul>
 *   <li>Successful API calls (200 OK with JSON response)</li>
 *   <li>Authentication errors (401 Unauthorized)</li>
 *   <li>Rate limiting (429 Too Many Requests)</li>
 *   <li>Server errors (500 Internal Server Error)</li>
 *   <li>Timeout handling</li>
 * </ul>
 * </p>
 *
 * <p>Note: Tests are disabled until AnthropicClaudeClient is implemented by Backend Architect.</p>
 */
@Disabled("Waiting for AnthropicClaudeClient implementation from Backend Architect")
public class AnthropicClaudeClientTest extends AbstractExternalClientTest {

    // TODO: Inject AnthropicClaudeClient once implemented
    // @Autowired
    // private AnthropicClaudeClient claudeClient;

    @Test
    void testSuccessfulApiCall() {
        // Given: Mock successful Claude API response
        String mockResponse = """
            {
              "id": "msg_01ABC123",
              "type": "message",
              "role": "assistant",
              "content": [
                {
                  "type": "text",
                  "text": "This opportunity has high relevance for AI/ML services."
                }
              ],
              "model": "claude-3-5-sonnet-20241022",
              "stop_reason": "end_turn",
              "usage": {
                "input_tokens": 150,
                "output_tokens": 50
              }
            }
            """;

        wireMockServer.stubFor(post(urlPathEqualTo("/v1/messages"))
                .withHeader(HttpHeaders.AUTHORIZATION, containing("Bearer"))
                .withHeader("anthropic-version", equalTo("2023-06-01"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(mockResponse)));

        // When: Call Claude API client
        // TODO: Implement once AnthropicClaudeClient exists
        // String result = claudeClient.analyzeOpportunity(prompt);

        // Then: Verify response parsed correctly
        // assertNotNull(result);
        // assertTrue(result.contains("high relevance"));

        // Verify API called with correct parameters
        wireMockServer.verify(postRequestedFor(urlPathEqualTo("/v1/messages"))
                .withHeader(HttpHeaders.AUTHORIZATION, containing("Bearer"))
                .withHeader("anthropic-version", equalTo("2023-06-01"))
                .withHeader(HttpHeaders.CONTENT_TYPE, containing(MediaType.APPLICATION_JSON_VALUE)));
    }

    @Test
    void testUnauthorizedError() {
        // Given: Mock 401 Unauthorized response
        wireMockServer.stubFor(post(urlPathEqualTo("/v1/messages"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                            {
                              "type": "error",
                              "error": {
                                "type": "authentication_error",
                                "message": "invalid x-api-key"
                              }
                            }
                            """)));

        // When/Then: Expect exception for unauthorized access
        // TODO: Implement once AnthropicClaudeClient exists
        // assertThrows(AuthenticationException.class, () -> {
        //     claudeClient.analyzeOpportunity(prompt);
        // });
    }

    @Test
    void testRateLimitError() {
        // Given: Mock 429 Rate Limited response
        wireMockServer.stubFor(post(urlPathEqualTo("/v1/messages"))
                .willReturn(aResponse()
                        .withStatus(429)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withHeader("retry-after", "60")
                        .withBody("""
                            {
                              "type": "error",
                              "error": {
                                "type": "rate_limit_error",
                                "message": "Rate limit exceeded"
                              }
                            }
                            """)));

        // When/Then: Expect exception with retry information
        // TODO: Implement once AnthropicClaudeClient exists
        // RateLimitException ex = assertThrows(RateLimitException.class, () -> {
        //     claudeClient.analyzeOpportunity(prompt);
        // });
        // assertEquals(60, ex.getRetryAfterSeconds());
    }

    @Test
    void testServerError() {
        // Given: Mock 500 Internal Server Error
        wireMockServer.stubFor(post(urlPathEqualTo("/v1/messages"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                            {
                              "type": "error",
                              "error": {
                                "type": "internal_server_error",
                                "message": "An internal server error occurred"
                              }
                            }
                            """)));

        // When/Then: Expect exception for server error
        // TODO: Implement once AnthropicClaudeClient exists
        // assertThrows(ApiException.class, () -> {
        //     claudeClient.analyzeOpportunity(prompt);
        // });
    }

    @Test
    void testTimeoutHandling() {
        // Given: Mock delayed response (exceeds timeout)
        wireMockServer.stubFor(post(urlPathEqualTo("/v1/messages"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{}")
                        .withFixedDelay(10000))); // 10 second delay

        // When/Then: Expect timeout exception
        // TODO: Implement once AnthropicClaudeClient exists
        // assertThrows(TimeoutException.class, () -> {
        //     claudeClient.analyzeOpportunity(prompt);
        // }, "API call should timeout after configured duration");
    }
}
