package com.athena.core.client;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SmtpEmailClient.
 * Uses mocked JavaMailSender to verify email sending logic.
 */
class SmtpEmailClientTest {

    private JavaMailSender mockMailSender;
    private SmtpEmailClient emailClient;
    private MimeMessage mockMimeMessage;

    @BeforeEach
    void setUp() {
        mockMailSender = mock(JavaMailSender.class);
        mockMimeMessage = mock(MimeMessage.class);

        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);

        emailClient = new SmtpEmailClient(mockMailSender, "test@athena.local", true);
    }

    @Test
    void testSendTextEmail_success() {
        // Execute
        emailClient.sendTextEmail("user@example.com", "Test Subject", "Test body");

        // Verify mail sender was called
        verify(mockMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void testSendHtmlEmail_success() {
        // Execute
        emailClient.sendHtmlEmail("user@example.com", "Test Subject", "<h1>HTML Body</h1>");

        // Verify mail sender was called
        verify(mockMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void testSendOpportunityAlert_success() {
        // Execute
        emailClient.sendOpportunityAlert(
            "user@example.com",
            "Cloud Infrastructure Services",
            88,
            "SOL-2025-001",
            LocalDate.of(2025, 3, 15),
            "https://sam.gov/opp/123"
        );

        // Verify mail sender was called
        verify(mockMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void testSendWeeklyDigest_success() {
        // Create opportunity summaries
        List<SmtpEmailClient.OpportunitySummary> opportunities = Arrays.asList(
            new SmtpEmailClient.OpportunitySummary("Opportunity 1", 85, LocalDate.of(2025, 3, 1)),
            new SmtpEmailClient.OpportunitySummary("Opportunity 2", 92, LocalDate.of(2025, 3, 5)),
            new SmtpEmailClient.OpportunitySummary("Opportunity 3", 78, LocalDate.of(2025, 3, 10))
        );

        // Execute
        emailClient.sendWeeklyDigest(
            "user@example.com",
            LocalDate.of(2025, 2, 24),
            LocalDate.of(2025, 3, 2),
            opportunities
        );

        // Verify mail sender was called
        verify(mockMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void testSendTeamNotification_success() {
        // Execute
        emailClient.sendTeamNotification(
            "user@example.com",
            "Alpha Team",
            "Cloud Services RFP",
            "Proposal review meeting scheduled for tomorrow at 2 PM"
        );

        // Verify mail sender was called
        verify(mockMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void testSendTextEmail_disabled() {
        // Create disabled client
        SmtpEmailClient disabledClient = new SmtpEmailClient(mockMailSender, "test@athena.local", false);

        // Execute
        disabledClient.sendTextEmail("user@example.com", "Subject", "Body");

        // Verify mail sender was NOT called
        verify(mockMailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void testSendHtmlEmail_disabled() {
        // Create disabled client
        SmtpEmailClient disabledClient = new SmtpEmailClient(mockMailSender, "test@athena.local", false);

        // Execute
        disabledClient.sendHtmlEmail("user@example.com", "Subject", "<p>Body</p>");

        // Verify mail sender was NOT called
        verify(mockMailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void testSendTextEmail_messagingException() {
        // Mock exception during send
        doThrow(new RuntimeException("SMTP connection failed"))
            .when(mockMailSender).send(any(MimeMessage.class));

        // Execute and verify exception
        assertThrows(SmtpEmailClient.EmailException.class, () -> {
            emailClient.sendTextEmail("user@example.com", "Subject", "Body");
        });
    }

    @Test
    void testSendHtmlEmail_messagingException() {
        // Mock exception during send
        doThrow(new RuntimeException("SMTP connection failed"))
            .when(mockMailSender).send(any(MimeMessage.class));

        // Execute and verify exception
        assertThrows(SmtpEmailClient.EmailException.class, () -> {
            emailClient.sendHtmlEmail("user@example.com", "Subject", "<p>Body</p>");
        });
    }

    @Test
    void testOpportunitySummary_getters() {
        // Create summary
        SmtpEmailClient.OpportunitySummary summary = new SmtpEmailClient.OpportunitySummary(
            "Test Opportunity",
            85,
            LocalDate.of(2025, 3, 15)
        );

        // Verify getters
        assertEquals("Test Opportunity", summary.getTitle());
        assertEquals(85, summary.getScore());
        assertEquals(LocalDate.of(2025, 3, 15), summary.getDeadline());
    }

    @Test
    void testEmailException_withCause() {
        // Create exception with cause
        Exception cause = new MessagingException("Root cause");
        SmtpEmailClient.EmailException exception = new SmtpEmailClient.EmailException("Email failed", cause);

        // Verify
        assertEquals("Email failed", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
