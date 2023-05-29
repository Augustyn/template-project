rootProject.name = "itm-demo"

pluginManagement {
    repositories {
        mavenCentral()
    }
    plugins {
        id("org.springframework.boot") version "3.0.5"
        `version-catalog`
//        id("org.gradle.toolchains.foojay-resolver") version "0.4.0"
//        id("org.gradle.toolchains.foojay-resolver-convention") version("0.4.0")
    }
}
//plugins {
//    id("org.gradle.toolchains.foojay-resolver-convention") version("0.4.0")
//}
//toolchainManagement {
//    jvm {
//        javaRepositories {
//            repository("foojay") {
//                resolverClass.set(org.gradle.toolchains.foojay.FoojayToolchainResolver::class.java)
//            }
//        }
//    }
//}
include(
    "bom",
    "infrastructure:spring",
    "application",
    "infrastructure:frontend",
    "infrastructure:dba",
)
findProject("infrastructure:dba")?.name = "database"
