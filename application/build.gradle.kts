@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.jvm)
}

dependencies {
    implementation(project(":bom"))
    implementation(libs.slf4japi)
    testImplementation(libs.bundles.test)
}
