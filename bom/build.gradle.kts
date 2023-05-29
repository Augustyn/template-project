
dependencies {
    api(platform(libs.spring.bom))
    api(platform(libs.spring.boot.bom))
    api(platform(libs.junit.bom))
    api(platform(libs.slf4japi))
    constraints {
        implementation("org.apache.logging.log4j:log4j-core") {
            version {
                strictly("[2.19.0")
                prefer("latest.release")
            }
        }
    }
}