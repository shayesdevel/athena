package com.athena.core.client;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for SMTP email client.
 * Uses GreenMail embedded email server to test email sending without real SMTP server.
 *
 * <p>Tests cover:
 * <ul>
 *   <li>Plain text email sending</li>
 *   <li>HTML email sending</li>
 *   <li>Email templates (opportunity alerts, weekly digests)</li>
 *   <li>Recipient verification</li>
 *   <li>Subject and content validation</li>
 * </ul>
 * </p>
 *
 * <p>Note: Tests are disabled until SmtpEmailClient is implemented by Backend Architect.</p>
 */
@SpringBootTest
@ActiveProfiles("test")
@Disabled("Waiting for SmtpEmailClient implementation from Backend Architect")
public class SmtpEmailClientTest {

    /**
     * GreenMail extension provides embedded SMTP server for testing.
     * Server starts before each test and stops after.
     */
    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test@athena.local", "password"))
            .withPerMethodLifecycle(false);

    // TODO: Inject SmtpEmailClient once implemented
    // @Autowired
    // private SmtpEmailClient emailClient;

    @Test
    void testSendPlainTextEmail() throws MessagingException {
        // When: Send plain text email
        // TODO: Implement once SmtpEmailClient exists
        // emailClient.sendEmail(
        //     "recipient@example.com",
        //     "Test Subject",
        //     "This is a plain text email body."
        // );

        // Then: Verify email received
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        // assertEquals(1, receivedMessages.length);

        // MimeMessage message = receivedMessages[0];
        // assertEquals("Test Subject", message.getSubject());
        // assertEquals("recipient@example.com", message.getAllRecipients()[0].toString());
        // assertTrue(message.getContent().toString().contains("plain text email body"));
    }

    @Test
    void testSendHtmlEmail() throws MessagingException {
        // When: Send HTML email
        // TODO: Implement once SmtpEmailClient exists
        // String htmlContent = """
        //     <html>
        //     <body>
        //         <h1>Opportunity Alert</h1>
        //         <p>A new opportunity has been posted.</p>
        //     </body>
        //     </html>
        //     """;
        // emailClient.sendHtmlEmail(
        //     "recipient@example.com",
        //     "Opportunity Alert",
        //     htmlContent
        // );

        // Then: Verify HTML email received
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        // assertEquals(1, receivedMessages.length);

        // MimeMessage message = receivedMessages[0];
        // assertEquals("Opportunity Alert", message.getSubject());
        // assertTrue(message.getContentType().contains("text/html"));
        // assertTrue(message.getContent().toString().contains("<h1>Opportunity Alert</h1>"));
    }

    @Test
    void testOpportunityAlertTemplate() throws MessagingException {
        // Given: Opportunity details
        // TODO: Implement once SmtpEmailClient exists
        // String opportunityTitle = "IT Services for Federal Agency";
        // String solicitationNumber = "SOL-2025-001";
        // String deadline = "2025-12-15";
        // String link = "https://sam.gov/opp/SOL-2025-001";

        // When: Send opportunity alert email
        // emailClient.sendOpportunityAlert(
        //     "user@example.com",
        //     opportunityTitle,
        //     solicitationNumber,
        //     deadline,
        //     link
        // );

        // Then: Verify email contains all details
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        // assertEquals(1, receivedMessages.length);

        // MimeMessage message = receivedMessages[0];
        // assertTrue(message.getSubject().contains(solicitationNumber));
        // String content = message.getContent().toString();
        // assertTrue(content.contains(opportunityTitle));
        // assertTrue(content.contains(deadline));
        // assertTrue(content.contains(link));
    }

    @Test
    void testWeeklyDigestTemplate() throws MessagingException {
        // Given: Weekly digest data
        // TODO: Implement once SmtpEmailClient exists
        // List<OpportunitySummary> opportunities = List.of(
        //     new OpportunitySummary("SOL-001", "IT Services", "2025-12-15"),
        //     new OpportunitySummary("SOL-002", "Consulting", "2025-12-20")
        // );

        // When: Send weekly digest
        // emailClient.sendWeeklyDigest("user@example.com", opportunities, "2025-11-15");

        // Then: Verify digest contains all opportunities
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        // assertEquals(1, receivedMessages.length);

        // MimeMessage message = receivedMessages[0];
        // assertTrue(message.getSubject().contains("Weekly Digest"));
        // String content = message.getContent().toString();
        // assertTrue(content.contains("SOL-001"));
        // assertTrue(content.contains("SOL-002"));
        // assertTrue(content.contains("IT Services"));
        // assertTrue(content.contains("Consulting"));
    }

    @Test
    void testMultipleRecipients() throws MessagingException {
        // When: Send email to multiple recipients
        // TODO: Implement once SmtpEmailClient exists
        // emailClient.sendEmail(
        //     List.of("user1@example.com", "user2@example.com", "user3@example.com"),
        //     "Team Alert",
        //     "This is a team-wide notification."
        // );

        // Then: Verify all recipients received email
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        // assertEquals(3, receivedMessages.length);

        // for (MimeMessage message : receivedMessages) {
        //     assertEquals("Team Alert", message.getSubject());
        //     assertTrue(message.getContent().toString().contains("team-wide notification"));
        // }
    }

    @Test
    void testEmailSendingFailure() {
        // Given: Invalid SMTP configuration (wrong host/port)
        // TODO: Implement once SmtpEmailClient exists

        // When/Then: Expect exception for SMTP failure
        // assertThrows(EmailSendException.class, () -> {
        //     emailClient.sendEmail("recipient@example.com", "Subject", "Body");
        // });
    }
}
