plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":athena-core"))
    implementation(project(":athena-common"))

    // Spring Boot - Web MVC
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.0")

    // Spring Boot - Validation
    implementation("org.springframework.boot:spring-boot-starter-validation:3.2.0")

    // Spring Boot - Security
    implementation("org.springframework.boot:spring-boot-starter-security:3.2.0")

    // Spring Boot - Actuator (monitoring)
    implementation("org.springframework.boot:spring-boot-starter-actuator:3.2.0")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")

    // OpenAPI documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

    // HTTP client
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.2.0")

    // Micrometer (metrics)
    implementation("io.micrometer:micrometer-registry-prometheus:1.12.1")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.0")
    testImplementation("org.springframework.security:spring-security-test:6.2.1")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
}
