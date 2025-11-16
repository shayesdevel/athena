package com.athena.core.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Abstract base class for external HTTP client integration tests.
 * Provides WireMock server for mocking external API responses.
 *
 * <p>Tests extending this class can use WireMock to stub HTTP responses
 * from external services (Claude API, Teams webhook, etc.) without making
 * real network calls.</p>
 *
 * <p>The WireMock server is started before each test and stopped after,
 * ensuring test isolation.</p>
 */
@SpringBootTest
@ActiveProfiles("test")
public abstract class AbstractExternalClientTest {

    protected WireMockServer wireMockServer;
    protected String baseUrl;

    /**
     * Starts WireMock server before each test.
     * Server runs on a random port to avoid conflicts.
     */
    @BeforeEach
    void startWireMock() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMockServer.start();
        baseUrl = "http://localhost:" + wireMockServer.port();
    }

    /**
     * Stops WireMock server after each test.
     * Ensures clean state for next test.
     */
    @AfterEach
    void stopWireMock() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }
}
