# External Client Integration Tests

This directory contains integration tests for external API clients and data loaders.

## Test Infrastructure

### WireMock HTTP Mocking

**Purpose**: Mock external HTTP APIs (Claude, Teams) without making real network calls

**Setup**: `AbstractExternalClientTest` base class provides:
- WireMock server started before each test on random port
- Automatic cleanup after each test
- Base URL property for client configuration

**Usage**:
```java
@Test
void testApiCall() {
    wireMockServer.stubFor(post("/endpoint")
        .willReturn(aResponse()
            .withStatus(200)
            .withBody("{...}")));

    // Call your client
    // Verify with wireMockServer.verify(...)
}
```

### GreenMail SMTP Testing

**Purpose**: Test email sending without real SMTP server

**Setup**: `SmtpEmailClientTest` uses `@RegisterExtension` for embedded SMTP server

**Usage**:
```java
@RegisterExtension
static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
    .withConfiguration(GreenMailConfiguration.aConfig().withUser("test", "password"))
    .withPerMethodLifecycle(false);

@Test
void testEmail() {
    // Send email via client
    MimeMessage[] messages = greenMail.getReceivedMessages();
    assertEquals(1, messages.length);
}
```

### Testcontainers PostgreSQL

**Purpose**: Test database persistence with real PostgreSQL (for SamGovDataLoader)

**Setup**: `AbstractIntegrationTest` provides singleton PostgreSQL container

**Usage**:
```java
public class SamGovDataLoaderTest extends AbstractIntegrationTest {
    @Autowired
    private OpportunityRepository repository;

    @Test
    void testLoad() {
        // Load data
        // Verify with repository.findAll()
    }
}
```

## Test Classes

### AnthropicClaudeClientTest
**Status**: Ready (disabled until client implemented)
**Coverage**:
- Successful API calls (200 OK)
- Authentication errors (401)
- Rate limiting (429)
- Server errors (500)
- Timeout handling

**Dependencies**:
- WireMock for API mocking
- Spring WebClient (implementation dependency)

### MicrosoftTeamsClientTest
**Status**: Ready (disabled until client implemented)
**Coverage**:
- Adaptive card sending (200 OK)
- Bad request errors (400)
- Webhook not found (404)
- Retry on transient failures (503)
- Adaptive card structure validation

**Dependencies**:
- WireMock for webhook mocking
- Spring WebClient (implementation dependency)

### SmtpEmailClientTest
**Status**: Ready (disabled until client implemented)
**Coverage**:
- Plain text email sending
- HTML email sending
- Email templates (opportunity alerts, weekly digests)
- Multiple recipients
- SMTP failure handling

**Dependencies**:
- GreenMail embedded email server
- Spring Boot Mail starter

### SamGovDataLoaderTest
**Status**: Ready (disabled until loader implemented)
**Coverage**:
- Valid JSON parsing and persistence
- Malformed JSON error handling
- Missing required fields validation
- Missing file error handling
- Multiple opportunity loading
- Directory loading
- Duplicate handling
- JSON field mapping verification

**Dependencies**:
- Testcontainers PostgreSQL
- Jackson JSON parser
- Sample JSON files in `src/test/resources/samgov-data/`

## Test Data Files

Location: `athena-core/src/test/resources/samgov-data/`

- `valid-opportunity.json` - Complete SAM.gov opportunity with all fields
- `invalid-opportunity.json` - Missing required fields
- `malformed.json` - Malformed JSON (missing closing brace)

## Running Tests

```bash
# Run all client tests (currently all disabled)
./gradlew :athena-core:test --tests "com.athena.core.client.*"

# Run specific test class
./gradlew :athena-core:test --tests "AnthropicClaudeClientTest"

# Run once client is implemented (remove @Disabled annotation first)
./gradlew :athena-core:test --tests "AnthropicClaudeClientTest"
```

## Enabling Tests

Once Backend Architect implements a client:

1. Remove `@Disabled` annotation from test class
2. Uncomment TODO blocks in test methods
3. Inject client via `@Autowired`
4. Configure client to use `baseUrl` from WireMock (or GreenMail for SMTP)
5. Run tests: `./gradlew :athena-core:test --tests "{ClassName}"`

## Dependencies Added

In `athena-core/build.gradle.kts`:

```kotlin
// Implementation dependencies (for Backend Architect's clients)
implementation("org.springframework.boot:spring-boot-starter-webflux:3.2.0")
implementation("org.springframework.boot:spring-boot-starter-mail:3.2.0")
implementation("com.fasterxml.jackson.core:jackson-databind:2.15.3")

// Test dependencies
testImplementation("org.wiremock:wiremock-standalone:3.3.1")
testImplementation("com.icegreen:greenmail-junit5:2.0.1")
```

## Integration with Quality Gates

### Pre-Commit (Gate 3)
All tests must pass before commit (D009 protocol):
```bash
./gradlew :athena-core:test
```

### Coverage Requirements
Target: 80% code coverage for external clients

Once clients are implemented, verify with:
```bash
./gradlew :athena-core:test jacocoTestReport
open athena-core/build/reports/jacoco/test/html/index.html
```

## Notes for Backend Architect

### Client Configuration

**AnthropicClaudeClient**:
- Use `baseUrl` property for API endpoint (injected in tests)
- Configure timeout (5-10 seconds recommended)
- Handle rate limit headers (retry-after)

**MicrosoftTeamsClient**:
- Accept webhook URL as parameter
- Use adaptive card format (v1.5)
- Implement retry logic for 503 errors

**SmtpEmailClient**:
- Use Spring Boot `JavaMailSender`
- Support both plain text and HTML emails
- Template rendering for opportunity alerts/digests

**SamGovDataLoader**:
- Use Jackson `ObjectMapper` for JSON parsing
- Support single file and directory loading
- Handle duplicate noticeId (throw exception or update)
- Map all JSON fields per MEM-005 constraint

## Related Documentation

- **MEM-005**: SAM.gov Data Source constraint (cached JSON, no API)
- **MEM-006**: Testcontainers integration test configuration
- **D009 Protocol**: Commit verification (all tests must pass)
- **Quality Gates**: `docs/00-active/quality-gates.md`

---

**Created**: 2025-11-15
**Author**: QA Specialist
**Status**: Test infrastructure ready, awaiting client implementations
