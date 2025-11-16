package com.athena.core.client;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * SMTP email client using Spring Boot's JavaMailSender.
 *
 * Sends email notifications for:
 * - Opportunity alerts (new high-scoring opportunities)
 * - Weekly digest emails (summary of opportunities)
 * - Team notifications (capture team updates)
 *
 * Configuration (application.yml):
 * - spring.mail.host: SMTP server host
 * - spring.mail.port: SMTP port (587 for TLS, 465 for SSL)
 * - spring.mail.username: SMTP username
 * - spring.mail.password: SMTP password
 * - spring.mail.properties.mail.smtp.auth: true
 * - spring.mail.properties.mail.smtp.starttls.enable: true
 * - athena.email.from: Sender email address
 * - athena.email.enabled: Enable/disable email sending (default: true)
 */
@Component
public class SmtpEmailClient {

    private static final Logger logger = LoggerFactory.getLogger(SmtpEmailClient.class);

    private final JavaMailSender mailSender;
    private final String fromAddress;
    private final boolean enabled;

    public SmtpEmailClient(
            JavaMailSender mailSender,
            @Value("${athena.email.from:noreply@athena.local}") String fromAddress,
            @Value("${athena.email.enabled:true}") boolean enabled) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
        this.enabled = enabled;

        logger.info("Initialized SMTP email client (enabled: {}, from: {})", enabled, fromAddress);
    }

    /**
     * Send a simple text email.
     *
     * @param to Recipient email address
     * @param subject Email subject
     * @param body Email body (plain text)
     */
    public void sendTextEmail(String to, String subject, String body) {
        if (!enabled) {
            logger.debug("Email sending disabled, skipping text email to {}", to);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false);

            mailSender.send(message);
            logger.info("Sent text email to {}: {}", to, subject);

        } catch (Exception e) {
            logger.error("Failed to send text email to {}: {}", to, subject, e);
            throw new EmailException("Failed to send email", e);
        }
    }

    /**
     * Send an HTML email.
     *
     * @param to Recipient email address
     * @param subject Email subject
     * @param htmlBody Email body (HTML)
     */
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        if (!enabled) {
            logger.debug("Email sending disabled, skipping HTML email to {}", to);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = HTML content

            mailSender.send(message);
            logger.info("Sent HTML email to {}: {}", to, subject);

        } catch (Exception e) {
            logger.error("Failed to send HTML email to {}: {}", to, subject, e);
            throw new EmailException("Failed to send email", e);
        }
    }

    /**
     * Send opportunity alert email (high-scoring opportunity found).
     *
     * @param to Recipient email address
     * @param opportunityTitle Opportunity title
     * @param score AI-generated score
     * @param solicitationNumber Solicitation number
     * @param deadline Response deadline
     * @param opportunityUrl Link to opportunity details
     */
    public void sendOpportunityAlert(
            String to,
            String opportunityTitle,
            int score,
            String solicitationNumber,
            LocalDate deadline,
            String opportunityUrl) {

        String subject = String.format("High-Score Opportunity: %s (Score: %d)", opportunityTitle, score);

        String htmlBody = buildOpportunityAlertHtml(
            opportunityTitle,
            score,
            solicitationNumber,
            deadline,
            opportunityUrl
        );

        sendHtmlEmail(to, subject, htmlBody);
    }

    /**
     * Send weekly digest email with summary of opportunities.
     *
     * @param to Recipient email address
     * @param weekStart Start date of the week
     * @param weekEnd End date of the week
     * @param opportunities List of opportunities (title, score, deadline)
     */
    public void sendWeeklyDigest(
            String to,
            LocalDate weekStart,
            LocalDate weekEnd,
            List<OpportunitySummary> opportunities) {

        String subject = String.format("Athena Weekly Digest: %s - %s", weekStart, weekEnd);

        String htmlBody = buildWeeklyDigestHtml(weekStart, weekEnd, opportunities);

        sendHtmlEmail(to, subject, htmlBody);
    }

    /**
     * Send team notification email (capture team updates).
     *
     * @param to Recipient email address
     * @param teamName Team name
     * @param opportunityTitle Opportunity title
     * @param message Notification message
     */
    public void sendTeamNotification(
            String to,
            String teamName,
            String opportunityTitle,
            String message) {

        String subject = String.format("Team Update: %s - %s", teamName, opportunityTitle);

        String htmlBody = buildTeamNotificationHtml(teamName, opportunityTitle, message);

        sendHtmlEmail(to, subject, htmlBody);
    }

    // HTML template builders

    private String buildOpportunityAlertHtml(
            String title,
            int score,
            String solicitationNumber,
            LocalDate deadline,
            String url) {

        return String.format(
            "<!DOCTYPE html>" +
            "<html><head><meta charset='UTF-8'></head><body>" +
            "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
            "  <h2 style='color: #28a745;'>🎯 High-Score Opportunity Alert</h2>" +
            "  <h3>%s</h3>" +
            "  <table style='width: 100%%; border-collapse: collapse; margin-top: 20px;'>" +
            "    <tr><td style='padding: 10px; border-bottom: 1px solid #ddd;'><strong>Score:</strong></td>" +
            "        <td style='padding: 10px; border-bottom: 1px solid #ddd;'>%d/100</td></tr>" +
            "    <tr><td style='padding: 10px; border-bottom: 1px solid #ddd;'><strong>Solicitation:</strong></td>" +
            "        <td style='padding: 10px; border-bottom: 1px solid #ddd;'>%s</td></tr>" +
            "    <tr><td style='padding: 10px; border-bottom: 1px solid #ddd;'><strong>Deadline:</strong></td>" +
            "        <td style='padding: 10px; border-bottom: 1px solid #ddd;'>%s</td></tr>" +
            "  </table>" +
            "  <p style='margin-top: 30px;'>" +
            "    <a href='%s' style='background-color: #0076D7; color: white; padding: 12px 24px; " +
            "       text-decoration: none; border-radius: 4px; display: inline-block;'>View Opportunity</a>" +
            "  </p>" +
            "  <p style='color: #666; font-size: 12px; margin-top: 40px;'>" +
            "    This is an automated notification from Athena Federal Contract Intelligence Platform." +
            "  </p>" +
            "</div></body></html>",
            title,
            score,
            solicitationNumber,
            deadline != null ? deadline.toString() : "Not specified",
            url != null ? url : "#"
        );
    }

    private String buildWeeklyDigestHtml(
            LocalDate weekStart,
            LocalDate weekEnd,
            List<OpportunitySummary> opportunities) {

        StringBuilder tbody = new StringBuilder();
        for (OpportunitySummary opp : opportunities) {
            tbody.append(String.format(
                "<tr>" +
                "  <td style='padding: 10px; border-bottom: 1px solid #ddd;'>%s</td>" +
                "  <td style='padding: 10px; border-bottom: 1px solid #ddd; text-align: center;'>%d</td>" +
                "  <td style='padding: 10px; border-bottom: 1px solid #ddd; text-align: center;'>%s</td>" +
                "</tr>",
                opp.getTitle(),
                opp.getScore(),
                opp.getDeadline() != null ? opp.getDeadline().toString() : "N/A"
            ));
        }

        return String.format(
            "<!DOCTYPE html>" +
            "<html><head><meta charset='UTF-8'></head><body>" +
            "<div style='font-family: Arial, sans-serif; max-width: 800px; margin: 0 auto;'>" +
            "  <h2 style='color: #0076D7;'>📊 Athena Weekly Digest</h2>" +
            "  <p><strong>Period:</strong> %s to %s</p>" +
            "  <p><strong>Opportunities:</strong> %d new opportunities identified</p>" +
            "  <table style='width: 100%%; border-collapse: collapse; margin-top: 20px;'>" +
            "    <thead>" +
            "      <tr style='background-color: #f0f0f0;'>" +
            "        <th style='padding: 12px; text-align: left; border-bottom: 2px solid #ddd;'>Opportunity</th>" +
            "        <th style='padding: 12px; text-align: center; border-bottom: 2px solid #ddd;'>Score</th>" +
            "        <th style='padding: 12px; text-align: center; border-bottom: 2px solid #ddd;'>Deadline</th>" +
            "      </tr>" +
            "    </thead>" +
            "    <tbody>%s</tbody>" +
            "  </table>" +
            "  <p style='color: #666; font-size: 12px; margin-top: 40px;'>" +
            "    This is an automated weekly digest from Athena Federal Contract Intelligence Platform." +
            "  </p>" +
            "</div></body></html>",
            weekStart.toString(),
            weekEnd.toString(),
            opportunities.size(),
            tbody.toString()
        );
    }

    private String buildTeamNotificationHtml(
            String teamName,
            String opportunityTitle,
            String message) {

        return String.format(
            "<!DOCTYPE html>" +
            "<html><head><meta charset='UTF-8'></head><body>" +
            "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
            "  <h2 style='color: #ffc107;'>📢 Team Notification</h2>" +
            "  <h3>%s</h3>" +
            "  <p><strong>Opportunity:</strong> %s</p>" +
            "  <div style='background-color: #f9f9f9; padding: 15px; margin-top: 20px; border-left: 4px solid #ffc107;'>" +
            "    <p>%s</p>" +
            "  </div>" +
            "  <p style='color: #666; font-size: 12px; margin-top: 40px;'>" +
            "    This is an automated notification from Athena Federal Contract Intelligence Platform." +
            "  </p>" +
            "</div></body></html>",
            teamName,
            opportunityTitle,
            message
        );
    }

    // DTOs

    public static class OpportunitySummary {
        private final String title;
        private final int score;
        private final LocalDate deadline;

        public OpportunitySummary(String title, int score, LocalDate deadline) {
            this.title = title;
            this.score = score;
            this.deadline = deadline;
        }

        public String getTitle() { return title; }
        public int getScore() { return score; }
        public LocalDate getDeadline() { return deadline; }
    }

    public static class EmailException extends RuntimeException {
        public EmailException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
