dependencies {
    implementation(project(":bom"))
    implementation(project(":application"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation(libs.bundles.test)
}