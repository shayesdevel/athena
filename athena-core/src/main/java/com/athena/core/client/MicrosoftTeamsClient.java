package com.athena.core.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

/**
 * HTTP client for Microsoft Teams Incoming Webhooks.
 *
 * Sends adaptive card notifications to Teams channels for:
 * - High-scoring opportunities (AI score > threshold)
 * - Capture team alerts (deadlines, milestones)
 * - System notifications (sync completed, errors)
 *
 * Configuration:
 * - teams.webhook.url: Incoming webhook URL from application.yml
 * - teams.webhook.enabled: Enable/disable Teams notifications (default: true)
 *
 * Teams Webhook Documentation:
 * https://learn.microsoft.com/en-us/microsoftteams/platform/webhooks-and-connectors/how-to/add-incoming-webhook
 */
@Component
public class MicrosoftTeamsClient {

    private static final Logger logger = LoggerFactory.getLogger(MicrosoftTeamsClient.class);

    private final WebClient webClient;
    private final boolean enabled;
    private final ObjectMapper objectMapper;

    public MicrosoftTeamsClient(
            @Value("${teams.webhook.url:}") String webhookUrl,
            @Value("${teams.webhook.enabled:true}") boolean enabled,
            ObjectMapper objectMapper) {
        this.enabled = enabled;
        this.objectMapper = objectMapper;

        if (enabled && (webhookUrl == null || webhookUrl.isEmpty())) {
            logger.warn("Teams webhook enabled but URL not configured. Notifications will be skipped.");
            this.webClient = null;
        } else {
            this.webClient = WebClient.builder()
                .baseUrl(webhookUrl)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();

            logger.info("Initialized Microsoft Teams client (enabled: {})", enabled);
        }
    }

    /**
     * Send a simple text message to Teams.
     *
     * @param title Message title
     * @param text Message body
     */
    public void sendMessage(String title, String text) {
        if (!enabled || webClient == null) {
            logger.debug("Teams notifications disabled or not configured, skipping message");
            return;
        }

        MessageCard card = new MessageCard();
        card.setTitle(title);
        card.setText(text);
        card.setThemeColor("0076D7"); // Microsoft blue

        sendCard(card);
    }

    /**
     * Send notification for a high-scoring opportunity.
     *
     * @param opportunityTitle Opportunity title
     * @param score AI-generated score (0-100)
     * @param solicitationNumber Solicitation number
     * @param deadline Response deadline
     * @param opportunityUrl Link to opportunity details
     */
    public void sendHighScoreOpportunityAlert(
            String opportunityTitle,
            int score,
            String solicitationNumber,
            String deadline,
            String opportunityUrl) {

        if (!enabled || webClient == null) {
            logger.debug("Teams notifications disabled, skipping high-score alert");
            return;
        }

        MessageCard card = new MessageCard();
        card.setTitle("🎯 High-Score Opportunity: " + opportunityTitle);
        card.setText(String.format(
            "**Score:** %d/100\n\n" +
            "**Solicitation:** %s\n\n" +
            "**Deadline:** %s\n\n" +
            "Review this opportunity immediately.",
            score,
            solicitationNumber,
            deadline
        ));
        card.setThemeColor("28a745"); // Green for good opportunities

        if (opportunityUrl != null && !opportunityUrl.isEmpty()) {
            card.setPotentialAction(List.of(
                new ActionCard("View Details", opportunityUrl)
            ));
        }

        sendCard(card);
    }

    /**
     * Send notification for capture team milestone.
     *
     * @param teamName Team name
     * @param opportunityTitle Opportunity title
     * @param milestone Milestone description
     */
    public void sendCaptureTeamAlert(
            String teamName,
            String opportunityTitle,
            String milestone) {

        if (!enabled || webClient == null) {
            logger.debug("Teams notifications disabled, skipping capture team alert");
            return;
        }

        MessageCard card = new MessageCard();
        card.setTitle("📢 Capture Team Alert: " + teamName);
        card.setText(String.format(
            "**Opportunity:** %s\n\n" +
            "**Milestone:** %s",
            opportunityTitle,
            milestone
        ));
        card.setThemeColor("ffc107"); // Yellow/amber for alerts

        sendCard(card);
    }

    /**
     * Send notification for system events (sync completed, errors).
     *
     * @param eventType Event type (e.g., "Sync Completed", "Error")
     * @param message Event details
     * @param isError Whether this is an error notification
     */
    public void sendSystemNotification(String eventType, String message, boolean isError) {
        if (!enabled || webClient == null) {
            logger.debug("Teams notifications disabled, skipping system notification");
            return;
        }

        MessageCard card = new MessageCard();
        card.setTitle((isError ? "⚠️ " : "✅ ") + eventType);
        card.setText(message);
        card.setThemeColor(isError ? "dc3545" : "28a745"); // Red for errors, green for success

        sendCard(card);
    }

    /**
     * Send a MessageCard to Teams webhook.
     */
    private void sendCard(MessageCard card) {
        try {
            logger.debug("Sending message to Teams: {}", card.getTitle());

            String response = webClient.post()
                .bodyValue(card)
                .retrieve()
                .onStatus(
                    HttpStatusCode::is4xxClientError,
                    clientResponse -> {
                        logger.error("Client error from Teams webhook: {}", clientResponse.statusCode());
                        return Mono.error(new TeamsWebhookException("Client error: " + clientResponse.statusCode()));
                    }
                )
                .onStatus(
                    HttpStatusCode::is5xxServerError,
                    clientResponse -> {
                        logger.error("Server error from Teams webhook: {}", clientResponse.statusCode());
                        return Mono.error(new TeamsWebhookException("Server error: " + clientResponse.statusCode()));
                    }
                )
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(10))
                .block();

            logger.debug("Teams message sent successfully: {}", response);

        } catch (Exception e) {
            logger.error("Failed to send Teams message: {}", card.getTitle(), e);
            // Don't throw exception - Teams notifications should not break application flow
        }
    }

    // DTOs for Teams MessageCard format

    public static class MessageCard {
        @JsonProperty("@type")
        private String type = "MessageCard";

        @JsonProperty("@context")
        private String context = "https://schema.org/extensions";

        private String title;
        private String text;
        private String themeColor;
        private List<ActionCard> potentialAction;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getContext() { return context; }
        public void setContext(String context) { this.context = context; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public String getThemeColor() { return themeColor; }
        public void setThemeColor(String themeColor) { this.themeColor = themeColor; }
        public List<ActionCard> getPotentialAction() { return potentialAction; }
        public void setPotentialAction(List<ActionCard> potentialAction) { this.potentialAction = potentialAction; }
    }

    public static class ActionCard {
        @JsonProperty("@type")
        private String type = "OpenUri";
        private String name;
        private List<Target> targets;

        public ActionCard() {}

        public ActionCard(String name, String uri) {
            this.name = name;
            this.targets = List.of(new Target("default", uri));
        }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public List<Target> getTargets() { return targets; }
        public void setTargets(List<Target> targets) { this.targets = targets; }
    }

    public static class Target {
        private String os;
        private String uri;

        public Target() {}

        public Target(String os, String uri) {
            this.os = os;
            this.uri = uri;
        }

        public String getOs() { return os; }
        public void setOs(String os) { this.os = os; }
        public String getUri() { return uri; }
        public void setUri(String uri) { this.uri = uri; }
    }

    public static class TeamsWebhookException extends RuntimeException {
        public TeamsWebhookException(String message) {
            super(message);
        }

        public TeamsWebhookException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
