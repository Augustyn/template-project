rootProject.name = "project-template"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        id("org.springframework.boot") version "3.0.5"
        `version-catalog`
    }
}
include(
    "bom",
    "infrastructure:spring",
    "application",
    "infrastructure:frontend",
    "infrastructure:dba",
)
findProject("infrastructure:dba")?.name = "database"
