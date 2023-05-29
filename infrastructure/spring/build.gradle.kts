import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.jvm)
    id("org.springframework.boot") version "3.0.5"
    alias(libs.plugins.spring.kotlin)
    alias(libs.plugins.spring.jpa)
}

dependencies {
    implementation(project(":bom"))
    implementation(project(":application"))
    implementation(project(":infrastructure:dba"))
    implementation(project(":infrastructure:frontend"))

    implementation("org.springframework.boot:spring-boot-starter-web")

    // kotlin reflection library, that spring heavily depends on:
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    runtimeOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(libs.junit)
}

tasks.withType<KotlinCompile> {
    /**
     * Although Java does not allow one to express null-safety in its type-system, Spring Framework provides null-safety of the whole Spring Framework API via tooling-friendly annotations declared in the org.springframework.lang package. By default, types from Java APIs used in Kotlin are recognized as platform types for which null-checks are relaxed. Kotlin support for JSR 305 annotations + Spring nullability annotations provide null-safety for the whole Spring Framework API to Kotlin developers, with the advantage of dealing with null related issues at compile time.
     * This feature can be enabled by adding the -Xjsr305 compiler flag with the strict options:
     **/
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
    }
}