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
    implementation("org.glassfish:jakarta.el:4.0.2")

    // HTTP Client for external APIs
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.2.0")

    // JSON processing (Jackson already included in Spring Boot)
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // Email support
    implementation("org.springframework.boot:spring-boot-starter-mail:3.2.0")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.0")
    testImplementation("org.testcontainers:testcontainers:1.19.3")
    testImplementation("org.testcontainers:postgresql:1.19.3")
    testImplementation("org.testcontainers:junit-jupiter:1.19.3")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
}
