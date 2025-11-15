package com.athena.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for Athena.
 * AI-powered federal contract intelligence platform.
 */
@SpringBootApplication(scanBasePackages = {"com.athena.api", "com.athena.core"})
public class AthenaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AthenaApplication.class, args);
    }
}
