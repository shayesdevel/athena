plugins {
    id("org.springframework.boot") apply false
}

dependencies {
    implementation(project(":athena-common"))

    // Spring Boot - Data JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.2.0")

    // PostgreSQL + pgvector
    implementation("org.postgresql:postgresql:42.7.1")

    // Flyway migrations
    implementation("org.flywaydb:flyway-core:10.4.1")
    implementation("org.flywaydb:flyway-database-postgresql:10.4.1")

    // Bean Validation
    implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.0")
    testImplementation("org.testcontainers:testcontainers:1.19.3")
    testImplementation("org.testcontainers:postgresql:1.19.3")
    testImplementation("org.testcontainers:junit-jupiter:1.19.3")
}
