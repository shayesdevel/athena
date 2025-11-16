plugins {
    id("org.springframework.boot") apply false
}

dependencies {
    implementation(project(":athena-core"))
    implementation(project(":athena-common"))

    // Spring Boot - Batch
    implementation("org.springframework.boot:spring-boot-starter-batch:3.2.0")

    // Spring Boot - Task Scheduling
    implementation("org.springframework:spring-context:6.1.2")

    // Redis (for distributed locking, task coordination)
    implementation("org.springframework.boot:spring-boot-starter-data-redis:3.2.0")

    // PDF processing
    implementation("org.apache.pdfbox:pdfbox:3.0.1")

    // Excel export
    implementation("org.apache.poi:poi-ooxml:5.2.5")

    // Email (SMTP)
    implementation("org.springframework.boot:spring-boot-starter-mail:3.2.0")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.0")
    testImplementation("org.springframework.batch:spring-batch-test:5.1.0")
    testImplementation("org.testcontainers:testcontainers:1.19.3")
    testImplementation("org.testcontainers:postgresql:1.19.3")
    testImplementation("org.testcontainers:junit-jupiter:1.19.3")
}
