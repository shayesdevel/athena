package com.athena.core;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Abstract base class for integration tests using Testcontainers.
 * Provides a shared PostgreSQL container instance for all integration tests.
 *
 * <p>Tests extending this class will automatically have access to a PostgreSQL
 * database running in a Docker container, with proper Spring datasource configuration.</p>
 *
 * <p>The container is started once and reused across all test classes for performance.</p>
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    /**
     * Singleton PostgreSQL container instance shared across all tests.
     * Uses postgres:17-alpine for consistency with production environment.
     */
    private static final PostgreSQLContainer<?> postgresContainer;

    static {
        postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:17-alpine"))
                .withDatabaseName("athena_test")
                .withUsername("test")
                .withPassword("test")
                .withReuse(true);
        postgresContainer.start();
    }

    /**
     * Configures Spring Boot test properties to use the Testcontainers database.
     * This method is called by Spring's test framework before the ApplicationContext loads.
     *
     * @param registry the dynamic property registry to add properties to
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }
}
