package com.athena.core.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * HTTP client for Anthropic Claude API.
 *
 * Provides methods for:
 * - AI opportunity scoring (analyze opportunities and assign scores)
 * - Capture strategy generation (create win strategies)
 * - Competitive analysis (analyze competitors)
 *
 * Uses Spring WebClient for reactive HTTP calls with retry logic and error handling.
 *
 * Configuration:
 * - anthropic.api.key: API key from application.yml
 * - anthropic.api.base-url: Base URL (default: https://api.anthropic.com)
 * - anthropic.api.model: Model to use (default: claude-3-5-sonnet-20241022)
 */
@Component
public class AnthropicClaudeClient {

    private static final Logger logger = LoggerFactory.getLogger(AnthropicClaudeClient.class);

    private final WebClient webClient;
    private final String apiKey;
    private final String model;
    private final ObjectMapper objectMapper;

    public AnthropicClaudeClient(
            @Value("${anthropic.api.key}") String apiKey,
            @Value("${anthropic.api.base-url:https://api.anthropic.com}") String baseUrl,
            @Value("${anthropic.api.model:claude-3-5-sonnet-20241022}") String model,
            ObjectMapper objectMapper) {
        this.apiKey = apiKey;
        this.model = model;
        this.objectMapper = objectMapper;

        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("x-api-key", apiKey)
            .defaultHeader("anthropic-version", "2023-06-01")
            .build();

        logger.info("Initialized Anthropic Claude client with model: {}", model);
    }

    /**
     * Send a message to Claude and get a response.
     *
     * @param systemPrompt System prompt (role/context for the AI)
     * @param userMessage User message (the actual query/task)
     * @param maxTokens Maximum tokens in response (default: 4096)
     * @return Claude's response text
     */
    public String sendMessage(String systemPrompt, String userMessage, Integer maxTokens) {
        if (maxTokens == null) {
            maxTokens = 4096;
        }

        ClaudeRequest request = new ClaudeRequest();
        request.setModel(model);
        request.setMaxTokens(maxTokens);
        request.setSystem(systemPrompt);
        request.setMessages(List.of(
            new Message("user", userMessage)
        ));

        logger.debug("Sending message to Claude API (model: {}, max_tokens: {})", model, maxTokens);

        try {
            ClaudeResponse response = webClient.post()
                .uri("/v1/messages")
                .bodyValue(request)
                .retrieve()
                .onStatus(
                    HttpStatusCode::is4xxClientError,
                    clientResponse -> {
                        logger.error("Client error from Claude API: {}", clientResponse.statusCode());
                        return Mono.error(new ClaudeApiException("Client error: " + clientResponse.statusCode()));
                    }
                )
                .onStatus(
                    HttpStatusCode::is5xxServerError,
                    clientResponse -> {
                        logger.error("Server error from Claude API: {}", clientResponse.statusCode());
                        return Mono.error(new ClaudeApiException("Server error: " + clientResponse.statusCode()));
                    }
                )
                .bodyToMono(ClaudeResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                    .filter(throwable -> throwable instanceof ClaudeApiException)
                    .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                        new ClaudeApiException("Max retries exceeded"))
                )
                .timeout(Duration.ofSeconds(30))
                .block();

            if (response != null && response.getContent() != null && !response.getContent().isEmpty()) {
                String responseText = response.getContent().get(0).getText();
                logger.debug("Received response from Claude API ({} tokens used)", response.getUsage().getInputTokens() + response.getUsage().getOutputTokens());
                return responseText;
            } else {
                throw new ClaudeApiException("Empty response from Claude API");
            }

        } catch (Exception e) {
            logger.error("Error calling Claude API", e);
            throw new ClaudeApiException("Failed to call Claude API: " + e.getMessage(), e);
        }
    }

    /**
     * Score an opportunity using Claude AI.
     *
     * @param opportunityTitle Opportunity title
     * @param opportunityDescription Full description
     * @param companyCapabilities Company's capabilities/experience
     * @return AI-generated score and rationale
     */
    public OpportunityScoreResult scoreOpportunity(
            String opportunityTitle,
            String opportunityDescription,
            String companyCapabilities) {

        String systemPrompt = "You are an expert federal contract analyst. Your role is to evaluate " +
            "government contracting opportunities and score them based on fit, win probability, and strategic value.";

        String userMessage = String.format(
            "Analyze this federal contracting opportunity and provide a score (0-100) with rationale.\n\n" +
            "Opportunity Title: %s\n\n" +
            "Description: %s\n\n" +
            "Our Capabilities: %s\n\n" +
            "Provide your response in this exact format:\n" +
            "SCORE: [0-100]\n" +
            "RATIONALE: [Your analysis]",
            opportunityTitle,
            opportunityDescription,
            companyCapabilities
        );

        String response = sendMessage(systemPrompt, userMessage, 2048);
        return parseScoreResponse(response);
    }

    /**
     * Generate a capture strategy for an opportunity.
     *
     * @param opportunityTitle Opportunity title
     * @param opportunityDescription Full description
     * @param companyStrengths Company strengths/differentiators
     * @return AI-generated capture strategy
     */
    public String generateCaptureStrategy(
            String opportunityTitle,
            String opportunityDescription,
            String companyStrengths) {

        String systemPrompt = "You are an expert capture manager specializing in federal government contracts. " +
            "Your role is to develop winning strategies for government proposals.";

        String userMessage = String.format(
            "Create a detailed capture strategy for this federal contracting opportunity.\n\n" +
            "Opportunity: %s\n\n" +
            "Description: %s\n\n" +
            "Our Strengths: %s\n\n" +
            "Provide a comprehensive capture strategy covering: win themes, discriminators, " +
            "teaming approach, and risk mitigation.",
            opportunityTitle,
            opportunityDescription,
            companyStrengths
        );

        return sendMessage(systemPrompt, userMessage, 4096);
    }

    /**
     * Analyze competitors for an opportunity.
     *
     * @param opportunityTitle Opportunity title
     * @param competitorInfo Information about known competitors
     * @return AI-generated competitive analysis
     */
    public String analyzeCompetitors(String opportunityTitle, String competitorInfo) {
        String systemPrompt = "You are a competitive intelligence analyst specializing in federal government contracting.";

        String userMessage = String.format(
            "Analyze the competitive landscape for this opportunity.\n\n" +
            "Opportunity: %s\n\n" +
            "Known Competitors: %s\n\n" +
            "Provide analysis of competitor strengths, weaknesses, and our positioning strategy.",
            opportunityTitle,
            competitorInfo
        );

        return sendMessage(systemPrompt, userMessage, 3072);
    }

    /**
     * Parse score response from Claude.
     * Expected format: "SCORE: 85\nRATIONALE: ..."
     */
    private OpportunityScoreResult parseScoreResponse(String response) {
        try {
            String[] lines = response.split("\n", 2);
            int score = 0;
            String rationale = response;

            for (String line : response.split("\n")) {
                if (line.startsWith("SCORE:")) {
                    String scoreStr = line.substring("SCORE:".length()).trim();
                    score = Integer.parseInt(scoreStr);
                } else if (line.startsWith("RATIONALE:")) {
                    rationale = line.substring("RATIONALE:".length()).trim();
                }
            }

            return new OpportunityScoreResult(score, rationale);

        } catch (Exception e) {
            logger.warn("Failed to parse score response, using defaults", e);
            return new OpportunityScoreResult(0, response);
        }
    }

    // DTOs for Claude API

    public static class ClaudeRequest {
        private String model;
        @JsonProperty("max_tokens")
        private Integer maxTokens;
        private String system;
        private List<Message> messages;

        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public Integer getMaxTokens() { return maxTokens; }
        public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }
        public String getSystem() { return system; }
        public void setSystem(String system) { this.system = system; }
        public List<Message> getMessages() { return messages; }
        public void setMessages(List<Message> messages) { this.messages = messages; }
    }

    public static class Message {
        private String role;
        private String content;

        public Message() {}
        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    public static class ClaudeResponse {
        private String id;
        private String type;
        private String role;
        private List<ContentBlock> content;
        private String model;
        @JsonProperty("stop_reason")
        private String stopReason;
        private Usage usage;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public List<ContentBlock> getContent() { return content; }
        public void setContent(List<ContentBlock> content) { this.content = content; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public String getStopReason() { return stopReason; }
        public void setStopReason(String stopReason) { this.stopReason = stopReason; }
        public Usage getUsage() { return usage; }
        public void setUsage(Usage usage) { this.usage = usage; }
    }

    public static class ContentBlock {
        private String type;
        private String text;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }

    public static class Usage {
        @JsonProperty("input_tokens")
        private Integer inputTokens;
        @JsonProperty("output_tokens")
        private Integer outputTokens;

        public Integer getInputTokens() { return inputTokens; }
        public void setInputTokens(Integer inputTokens) { this.inputTokens = inputTokens; }
        public Integer getOutputTokens() { return outputTokens; }
        public void setOutputTokens(Integer outputTokens) { this.outputTokens = outputTokens; }
    }

    public static class OpportunityScoreResult {
        private final int score;
        private final String rationale;

        public OpportunityScoreResult(int score, String rationale) {
            this.score = score;
            this.rationale = rationale;
        }

        public int getScore() { return score; }
        public String getRationale() { return rationale; }
    }

    public static class ClaudeApiException extends RuntimeException {
        public ClaudeApiException(String message) {
            super(message);
        }

        public ClaudeApiException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
