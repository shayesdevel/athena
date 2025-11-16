plugins {
    id("org.springframework.boot") apply false
}

dependencies {
    implementation(project(":athena-core"))
    implementation(project(":athena-common"))

    // Spring Boot - Batch
    implementation("org.springframework.boot:spring-boot-starter-batch:3.2.0")

    // Spring Boot - Core (for @Scheduled)
    implementation("org.springframework.boot:spring-boot-starter:3.2.0")

    // Spring Data JPA (for repository builders used in batch jobs)
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.2.0")

    // JSON processing (needed for SAM.gov data parsing)
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.3")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.3")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.0")
    testImplementation("org.springframework.batch:spring-batch-test:5.1.0")
    testImplementation("org.testcontainers:testcontainers:1.19.3")
    testImplementation("org.testcontainers:postgresql:1.19.3")
    testImplementation("org.testcontainers:junit-jupiter:1.19.3")
}
