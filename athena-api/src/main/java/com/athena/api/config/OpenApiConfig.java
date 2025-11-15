package com.athena.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for Athena API documentation.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI athenaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Athena API")
                        .version("0.1.0")
                        .description("AI-powered federal contract intelligence platform - REST API for government contract discovery, AI scoring, teaming intelligence, and competitive analysis")
                        .contact(new Contact()
                                .name("Athena Team")
                                .url("https://github.com/shayesdevel/athena")));
    }
}
