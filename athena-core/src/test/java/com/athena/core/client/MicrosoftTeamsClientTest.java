package com.athena.core.client;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Microsoft Teams webhook client.
 * Uses WireMock to mock Teams webhook endpoint.
 *
 * <p>Tests cover:
 * <ul>
 *   <li>Successful adaptive card posting (200 OK)</li>
 *   <li>Bad request errors (400 Bad Request)</li>
 *   <li>Webhook not found (404 Not Found)</li>
 *   <li>Retry logic for transient failures</li>
 * </ul>
 * </p>
 *
 * <p>Note: Tests are disabled until MicrosoftTeamsClient is implemented by Backend Architect.</p>
 */
@Disabled("Waiting for MicrosoftTeamsClient implementation from Backend Architect")
public class MicrosoftTeamsClientTest extends AbstractExternalClientTest {

    // TODO: Inject MicrosoftTeamsClient once implemented
    // @Autowired
    // private MicrosoftTeamsClient teamsClient;

    @Test
    void testSuccessfulAdaptiveCardSend() {
        // Given: Mock successful Teams webhook response
        String webhookPath = "/webhook/abc123";
        wireMockServer.stubFor(post(urlPathEqualTo(webhookPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("1")));

        // When: Send adaptive card notification
        // TODO: Implement once MicrosoftTeamsClient exists
        // String webhookUrl = baseUrl + webhookPath;
        // boolean result = teamsClient.sendNotification(webhookUrl, "New Opportunity Alert", "Check SAM.gov for details");

        // Then: Verify success
        // assertTrue(result);

        // Verify webhook called with correct payload
        wireMockServer.verify(postRequestedFor(urlPathEqualTo(webhookPath))
                .withHeader(HttpHeaders.CONTENT_TYPE, containing(MediaType.APPLICATION_JSON_VALUE))
                .withRequestBody(containing("@type"))
                .withRequestBody(containing("AdaptiveCard")));
    }

    @Test
    void testBadRequestError() {
        // Given: Mock 400 Bad Request (invalid card format)
        String webhookPath = "/webhook/abc123";
        wireMockServer.stubFor(post(urlPathEqualTo(webhookPath))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody("Invalid card format")));

        // When/Then: Expect exception for invalid request
        // TODO: Implement once MicrosoftTeamsClient exists
        // String webhookUrl = baseUrl + webhookPath;
        // assertThrows(BadRequestException.class, () -> {
        //     teamsClient.sendNotification(webhookUrl, null, "Missing title");
        // });
    }

    @Test
    void testWebhookNotFound() {
        // Given: Mock 404 Not Found (webhook deleted or invalid URL)
        String webhookPath = "/webhook/invalid";
        wireMockServer.stubFor(post(urlPathEqualTo(webhookPath))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBody("Webhook not found")));

        // When/Then: Expect exception for webhook not found
        // TODO: Implement once MicrosoftTeamsClient exists
        // String webhookUrl = baseUrl + webhookPath;
        // assertThrows(NotFoundException.class, () -> {
        //     teamsClient.sendNotification(webhookUrl, "Alert", "Message");
        // });
    }

    @Test
    void testRetryOnTransientFailure() {
        // Given: Mock 503 Service Unavailable, then success on retry
        String webhookPath = "/webhook/abc123";
        wireMockServer.stubFor(post(urlPathEqualTo(webhookPath))
                .inScenario("Retry")
                .whenScenarioStateIs("Started")
                .willReturn(aResponse()
                        .withStatus(503)
                        .withBody("Service temporarily unavailable"))
                .willSetStateTo("First attempt failed"));

        wireMockServer.stubFor(post(urlPathEqualTo(webhookPath))
                .inScenario("Retry")
                .whenScenarioStateIs("First attempt failed")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("1")));

        // When: Send notification (should retry on 503)
        // TODO: Implement once MicrosoftTeamsClient exists
        // String webhookUrl = baseUrl + webhookPath;
        // boolean result = teamsClient.sendNotification(webhookUrl, "Alert", "Message");

        // Then: Verify success after retry
        // assertTrue(result);

        // Verify two requests made (initial + retry)
        wireMockServer.verify(2, postRequestedFor(urlPathEqualTo(webhookPath)));
    }

    @Test
    void testAdaptiveCardStructure() {
        // Given: Mock successful response
        String webhookPath = "/webhook/abc123";
        wireMockServer.stubFor(post(urlPathEqualTo(webhookPath))
                .willReturn(aResponse().withStatus(200).withBody("1")));

        // When: Send notification with details
        // TODO: Implement once MicrosoftTeamsClient exists
        // String webhookUrl = baseUrl + webhookPath;
        // teamsClient.sendOpportunityAlert(webhookUrl, "SOL-2025-001", "IT Services", "$500K", "2025-12-15");

        // Then: Verify adaptive card contains expected fields
        wireMockServer.verify(postRequestedFor(urlPathEqualTo(webhookPath))
                .withRequestBody(matchingJsonPath("$.type", equalTo("message")))
                .withRequestBody(matchingJsonPath("$.attachments[0].contentType", equalTo("application/vnd.microsoft.card.adaptive")))
                .withRequestBody(matchingJsonPath("$.attachments[0].content.body[?(@.text =~ /SOL-2025-001/)]")));
    }
}
