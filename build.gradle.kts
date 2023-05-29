import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.nio.charset.StandardCharsets

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.jvm)
}
val groupId = "org.example.itm"
val appVersion = "1.0-SNAPSHOT"
group = groupId
version = appVersion

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "version-catalog")

    repositories {
        mavenCentral()
    }

    group = groupId
    version = appVersion

    dependencies {
        kotlin("stdlib")
    }
    val jvmVersion = JavaVersion.VERSION_17
    val kotlinApiVersion = "1.8"
    tasks.test {
        useJUnitPlatform()
        testLogging.showExceptions = true
        testLogging {
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
        }
    }
    kotlin {
        jvmToolchain(jvmVersion.majorVersion.toInt())
    }
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            kotlinOptions {
                apiVersion = kotlinApiVersion
                javaParameters = true
            }
        }
    }
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(jvmVersion.majorVersion))
        }
    }
    tasks.withType<JavaCompile> {
        options.encoding = StandardCharsets.UTF_8.toString()
    }

}
