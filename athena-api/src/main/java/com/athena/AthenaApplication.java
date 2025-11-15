package com.athena;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Athena - AI-powered federal contract intelligence platform.
 * <p>
 * This is a Spring Boot application that provides REST APIs for:
 * - Federal opportunity discovery and search
 * - AI-powered opportunity scoring and analysis
 * - Capture team management
 * - Competitive intelligence
 * <p>
 * Prototype Configuration:
 * - Uses cached SAM.gov JSON data (no API key required for PoC)
 * - Demonstrates ELT → LLM → analysis workflow
 * - PostgreSQL 17 + pgvector for vector similarity search
 *
 * @see <a href="https://github.com/shayesdevel/athena">GitHub Repository</a>
 */
@SpringBootApplication
public class AthenaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AthenaApplication.class, args);
    }
}
