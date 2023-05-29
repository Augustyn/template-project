# Introduction

Starting a new project from scratch can be a challenging and time-consuming task, even for experienced developers. 

Without a template or a starting point, you may find yourself struggling to define the project structure, set up the build system, and configure the dependencies.

The best approach for developing a new application is to use a modular architecture that allows for flexibility and scalability. In this tutorial, we will be using Gradle and Kotlin DSL, to gain the flexibility beyond the maven project object model in the XML. We will create a Bill of Materials (BOM) similar to that of Maven, which will help keep the process of maintaining the one, correct version of dependencies in one place.

Let's dive into the code.

# First step towards multimodule application
First, let’s start from creating the basic Gradle template, then we will move forward.
```kotlin
plugins {
   kotlin("jvm") version "1.8.21" ➀
}

group = "org.example.dba" ②
version = "1.0-SNAPSHOT"

repositories {
   mavenCentral() ➂
}

dependencies {
   testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3") ➃
   testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")
}

tasks.getByName<Test>("test") {
   useJUnitPlatform() ➄
}
```
This is a basic setup, let’s take a look what it consists of:

1. Specifies that the project will use the "JVM" Kotlin plugin. The full plugin name we may use instead is: "`org.jetbrains.kotlin.jvm`". This will come in handy later on this tutorial.

    We may as well use the longer, full version ID:

    `id("kotlin.jvm")`

    But the shortest way of defining it is as in the first example above.
2. Each project has to have their group name, which is the equivalent of mavens _groupId_. The module name would be the equivalent of mavens _artifactId_.
3. We must define the repository where to find our dependencies. In corporate world, this is the section where you provide the repository URL.
4. An example dependency, in test scope, and for test runtime scope. Managing the dependencies, we'll take care, in the second part of this tutorial.
5. Adding a hint for test, so that we will use the JUnit test engine. 

## Adding first modules
Now let's create the necessary modules for our application, the `application` and `infrastructure` folders. These folders will help us organize our code and follow the principles of the Hexagonal Architecture pattern. The pattern, which emphasizes protecting the domain from external components. For more information regarding the hexagonal architecture, please refer to my [description of the hexagonal architecture concept](https://www.itmagination.com/blog/enable-flexibility-in-your-project)

The `application/gradle.build.kts` will be:
```kotlin
plugins {
    kotlin("jvm") version "1.8.21"
}
repositories {
    mavenCentral()
}
dependencies {
    kotlin("stdlib")
}
```
We'll get back to it later on, and the `infractructure` folder let's create a spring module, and frontend rest API module for it. So, `infrastructure/spring/build.gradle.kts` will look like:
```kotlin
plugins { ➀
    kotlin("jvm") version "1.8.21"
    id("org.springframework.boot") version "3.0.5"
    kotlin("plugin.spring") version "1.8.21"
    kotlin("plugin.jpa") version "1.8.21"
}
repositories {
    mavenCentral()
}
dependencies {
    kotlin("stdlib")
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.0.5"))
    implementation(project(":application")) ➁

    implementation("org.springframework.boot:spring-boot-starter-web")

    // kotlin reflection library, that spring heavily depends on:
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    runtimeOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> { ➂
    /**
     Although Java does not allow one to express null-safety in its type-system, Spring Framework provides null-safety of the whole Spring Framework API via tooling-friendly annotations declared in the org.springframework.lang package. By default, types from Java APIs used in Kotlin are recognized as platform types for which null-checks are relaxed. Kotlin support for JSR 305 annotations + Spring nullability annotations provide null-safety for the whole Spring Framework API to Kotlin developers, with the advantage of dealing with null related issues at compile time.
     This feature can be enabled by adding the -Xjsr305 compiler flag with the strict options:
     **/
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
    }
}
```
1. In this code, we've added the necessary Spring and Kotlin dependencies, as in the Spring [documentation](https://spring.io/guides/tutorials/spring-boot-kotlin/).

    One thing emerges instantly: there's a lot of repetition, and the DRY principle comes to our developers mind. We have repetition in both, defining the maven repositories, and then the dependency versions. It would be great to have a way of mitigating the burden of keeping the version up to date.
    
    The removal of repetition, as well as managing versions needs more space, so I'll move it to the next paragraph, first let's take a look at other notes.

2. We define spring dependencies. Two things worth to mention for this section:

    The use of `platform` keyword. It's a Gradle way of suggesting the dependency resolver to use this file to define the transitive dependencies. You may also change it to `enforcePlatform`, and that will try to override the transitive dependencies in other dependencies, if a different version is used. 

    The reference to other project with `project(":path:to:project")`. The path starts with root (the ":") and consist of folder names to a module.
3. We need to add some magic, that I will now break down:
    - First, we find a task with type of `KotlinCompile`.
    - We set the variable `kotlinOptions` that accepts a configuration lambda
    - We add a "`-Xjsr305=strict`" to property freeCompilerArgs.

   This flag, will give a Kotlin hint, to enable it's null safe compile time check for spring specific null safe annotation, resulting a more precise null safe type check in a build time.

We also need to add modules to our `settings.gradle.kts`, so that Gradle will respect them:
```kotlin
include(
    "application",
    "infrastructure:spring",
)
```

# Removing repetition in the configuration
As noted in previous section, there are a lot of repetition, both in defining the repositories, plugins, and there would be as well repetition in defining the Java and Kotlin compile options. Let's change it.

First thing we may do in order to narrow down the configuration repetition is adding a block of code that would configure every project in our application, let's open the `build.gradle.kts` and after defining the `version` variable let's add:

```kotlin

    allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm") ➀
    apply(plugin = "version-catalog")
    
    repositories {
        mavenCentral()
    }
    
    group =  "org.example"
    version = "1.0-SNAPSHOT"
    
    dependencies {
        kotlin("stdlib") ➁
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
```

1. In `allroject` tag, there is no `plugins` tag, so we need to add our plugins with `apply`. Unfortunately, it does not support short version, so we need to provide the full plugin version.
The plugin version itself, can be set in `settings.gradle.kts`
2. Dependency versions. You may be tempted to add shared dependencies here, but I wouldn't recommend that. There's always a subproject that does not need some dependencies. Defining them here is tempting, but very ugly.

   It may be used to share dependencies between all modules, but this, in my opinion should be minimal, it's better to keep dependencies near their usage, and there are better ways to do that, then the `allproject` block.

    You may define a variable with: `val kotlinVersion: String by project`. The exact value needs to be added to project `/gradle.properties` file in form: `key=value`.
    The variable definition needs to be put before the `allproject` block and then use it inside it the block, but that will become messy, especially when the number of dependencies grows.

   Unfortunately, defining the version variable does not work for the apply plugins. We may skip providing the version, by defining them in the same way we did at the beginning of a file, but in a `settings.gradle.kt`.

    Again, I do not recommend it. It will create an ugly wall of version definitions and then their usage

    This solution may be suitable for small projects where there are little libraries used, and we do not have a wall of variables and their usage defined. There is a way to define version in one place, and use it across the whole project, in many places, without the need of defining a new variable in each build script. I'll be addressing this issue in a [Gradle libraries](#managing-gradle-libraries) section.

The `allproject` is a great place to configure the both Kotlin, and Java compiler, to set the language level.

It's also a great place to configure the behavior of our tests. We explicitly point that we need our tests to use the JUnit engine, but we will use the _kotest_ that leverages the same engine.

It configures the Kotlin, maven repositories and dependencies, so it's safe to remove those configuration blocks, that were defined earlier. For the resulting `build.gradle.kts`, please take a look at the [repository](https://github.com/Augustyn/template-project/blob/v1/build.gradle.kts). 

# Managing Gradle libraries
Once the project grows, and then the number of dependencies grows, the time to manage them grows linearly. We may find ourselves in a situation where different modules are using different dependency version.

To mitigate that problem, we'll be using a single place to define all dependency versions. We could leverage the `gradle.properties` but as mentioned in [removing repetition in the configuration](#removing-repetition-in-the-configuration) section, it will eventually create the ugly wall of variable definitions, and the `gradle.properties` file will lose its only purpose of configuring the Gradle build system itself, not to mention the wall of variable definitions.

## Version catalog
First, we will address the problem of managing the versions in a single place. In order to do so, we'll leverage the Gradle version catalog capability.

To settings.gradle.kts, we need to add a plugin: `version-catalog`:

```kotlin
pluginManagement {
    repositories {
        mavenCentral()
    }
    plugins {
        `version-catalog`
    }
}
```
The Gradle lifecycle consists of two phases: preparation and building application. First part, preparation goal is to gather all necessary resources to understand project, fetch all plugins that are used. Therefore, we need to provide additional `repositories` tag. This tells Gradle where to find plugins we are using in our project. Then, in the `plugins` section, we list plugins that should be enabled. The `version-catalog` is a built-in plugin, and that's why we do not need to provide its ID nor version. The Gradle embedded plugin version will be used.

This enables us the default version catalog plugin configuration, which is defining the dependencies in a `gradle/libs.versions.toml` file. This tutorial won't cover the structure of this file, we'll be just adding some dependencies. It's enough to say that there are four main parts of file, with headers: `[versions]`, `[libraries]`, `[plugins]` and `[bundles]`. First defines version, second dependencies, third plugins and forth bundles together libraries defined earlier.

Having a `gradle/libs.versions.toml`, as below:
```yaml
[versions]
kotlin = "1.8.21"
spring-boot = "3.0.5"
spring = "6.0.8"
kotest = "5.6.2"

[libraries]
junit-bom = { module = "org.junit:junit-bom", version = "5.9.1" }
spring-bom = { module = "org.springframework:spring-framework-bom", version.ref = "spring" }
spring-boot-bom = { module = "org.springframework.boot:spring-boot-dependencies", version.ref = "spring-boot" }
junit = { module = "org.junit.jupiter:junit-jupiter-api", version = "5.9.3" }
kotest-jvm = { module = "io.kotest:kotest-runner-junit5-jvm", version.ref = "kotest" }
kotest-core = { module = "io.kotest:kotest-assertions-core-jvm", version.ref = "kotest" }
mockk = { module = "io.mockk:mockk-jvm", version = "1.13.5" }
slf4japi = { module = "org.slf4j:slf4j-api", version = "2.0.7" }
[plugins]
jvm = { id = "org.jetbrains.kotlin.jvm", version.ref = "kotlin" }
spring-boot = { id = "org.springframework.boot.spring-boot-gradle-plugin", version.ref = "spring-boot" }
spring-kotlin = { id = "org.jetbrains.kotlin.plugin.spring", version.ref = "kotlin" }
spring-jpa = { id = "org.jetbrains.kotlin.plugin.jpa", version.ref = "kotlin" }

[bundles]
test = ["kotest-jvm", "kotest-core", "mockk"]
```
We may use plugins aliases, and get `libs`, so default prefix for version library, then section `plugins`, then a dot separated name of a plugin (notice that in TOML file it is a dash separated). Let's update our spring module configuration `infrastructure/spring/build.gradle.kts`:
```kotlin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.spring.kotlin) ➀
    alias(libs.plugins.spring.jpa)
    id("org.springframework.boot") version "3.0.5"
}
dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.0.5"))
    implementation(project(":application")) ➁

    implementation("org.springframework.boot:spring-boot-starter-web")

    // kotlin reflection library, that spring heavily depends on:
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    runtimeOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(libs.junit)
}

```
1. The plugins require a special handling, therefore the `alias` method is used, to pass the library plugins ID.
    You may use the ID as well, I left the spring boot plugin here, in order to demonstrate it.
2. The libraries, are default, so we do not need to prefix it with anything, just `libs` to let Gradle know we are using version's library, and then the defined library version.
    The dependencies that are managed by `platform`, does not need version.

This eases the process of handling versions, as their definitions are in one place.

But what about frameworks that use hundreds of dependencies and their versions are managed with maven bill of material?

## Bill of materials
The Spring framework provides the solution for that problem. Using plugin "`org.springframework.boot.spring-boot-gradle-plugin`" in a project that uses spring promise the use of correct spring BOM and handling the dependencies in that module. What about other modules? What about other frameworks that doesn't provide their own plugins? What about forcing the transitive dependency update, for example if we encounter another `log4shell` vulnerability? 
The spring plugin may help to resolve all that issues, but I'll show a different way of managing the dependencies, more flexible in my opinion. If you know a better solution, please let me know in the comment section or thorough GitHub.

First, we'll create another module, let's call it 'bom', and it's `bom/build.gradle.kts`:
```kotlin
dependencies {
    api(platform(libs.spring.bom)) ➀
    api(platform(libs.spring.boot.bom))
    api(platform(libs.junit.bom))
    api(platform("org.junit:junit-bom:5.9.2"))
    api(platform("org.spockframework:spock-bom:2.4-M1-groovy-4.0"))
    constraints {
        implementation("org.apache.logging.log4j:log4j-core") { ➁
            version {
                strictly("[2.19.0")
                prefer("latest.release")
            }
        }
    }
}
```
1. First we use the `api` to tell Gradle, that we will be transitively exporting that dependency to other modules, and we leverage the `platform` to tell Gradle that this is a project which defines constraints for the various dependencies found in the different subprojects. 
    
    A combination of those two will manage all transitive dependencies.
2. We may define additional constraints for our dependency set. This tells Gradle to use log4j-core greater than 2.19.0, and prefer the latest available.

The use of it is straightforward, we just need to depend on a module, let's update our `infrastructure/spring/build.gradle.kts` and replace the `platform`, in dependencies block with:
```kotlin
    implementation(project(":bom"))
```

We don't need to provide version for dependency in implementation scope that's versions are provided transitively from our 'bom' module. 

# Multimodule application

Separating a project into modules is crucial for achieving a robust hexagonal architecture. The application module contains the core business logic, which remains agnostic to the infrastructure details. This separation ensures that the business rules are decoupled from external dependencies, allowing for easier testing, maintainability, and flexibility in the long run.

Our modules may be now populated to create a fully functional rest application, that will demonstrate the port and adapters philosophy.

Let's start from controller. Create a front-end module, the `infrastructure/frontend/build.gradle.kts` will contain:
```kotlin
dependencies {
    implementation(project(":bom")) ➀
    implementation(project(":application")) ➁
    implementation("org.springframework.boot:spring-boot-starter-web") ➂
    testImplementation(libs.bundles.test) ➃
}
```
1. We inherit all dependency versions from Bill Of Materials.
2. We need our business logic, the port-s that the module needs to oblige.
3. Additional spring dependencies, as this module is a Spring Rest module
4. We leverage the bundles of gradle version library to minimize the test dependencies repetition

Remember to update the `settings.gradle.kts`, so that the new module would be visible:
```kotlin
include(
    "bom",
    "application",
    "infrastructure:spring",
    "infrastructure:frontend",
)
```
Then, the controller, `com/example/infra/rest/controller/BlogController.kt`:
```kotlin
package com.example.infra.rest.controller

import com.example.domain.srv.DomainService
import com.example.infra.rest.adapter.toRestResponseModel
import com.example.infra.rest.model.ResponseModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class BlogController(private val domainService: DomainService) {

    @GetMapping("/{param}") ➀
    fun blog(@PathVariable("param") param: String): ResponseModel { ➁
        return ResponseModel(responseParam = domainService.handle(param).toRestResponseModel()) ➂
    }

}
```
1. A simple controller to demonstrate connections between component, that's why it only supports GET method, taking a path parameter as input.
2. The method is returning a specific `ResponseModel` object, that contains two fields. It will be converted to JSON response by Spring.
3. Calling a domain service with appropriate parameters. For simplicity, it is String, but could be domain specific model. Service returns a domain specific model, and we transform it with our adapter method '.toRestResponseModel()' to this module specific model. For adapter implementation, please visit source code.

Our domain service is implemented in `com/example/domain/srv/impl/DefaultDomainService.kt`, as:
```kotlin
package com.example.domain.srv.impl

import com.example.domain.model.DomainModel
import com.example.domain.srv.DomainService
import com.example.domain.storage.ParamStorage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.security.SecureRandom

private const val STRING_LENGTH = 10

class DefaultDomainService(private val storage: ParamStorage) : DomainService { ➀
    private val log: Logger = LoggerFactory.getLogger(DefaultDomainService::class.qualifiedName)
    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    private val random: SecureRandom = SecureRandom.getInstanceStrong()

    override fun handle(param: String): DomainModel {
        log.info("Handling request, with param $param")
        return storage.store(DomainModel(param + randomString(STRING_LENGTH)))  ➂
    }

    private fun randomString(length: Int): String = List(length) { charPool.random() }.joinToString("") ➁

}
```
1. The class is implementing the `DomainService` interface. The interface that is used by other, non-core domain modules.
2. The dummy, "domain logic" is applied, as a business implementation
3. The storage service is called, and a Domain specific model returned to the rest controller.

Now, the domain specific is calling another port, a `ParamStorage.` Its implementation is in another module, the `infrastructure/dba/build.gradle.kts`, as follows:
```kotlin
dependencies {
    implementation(project(":bom"))
    implementation(project(":application"))

    testImplementation(libs.bundles.test)
}
```
As it doesn't have a real database storage implementation, the Gradle file is similar to our rest module.
The `ParamStorage` dummy, in memory implementation, may look like:
```kotlin
package org.example.dba

import com.example.domain.model.DomainModel
import com.example.domain.storage.ParamStorage

class MemoryParamStorage(private val storage: MutableList<StorageModel> = mutableListOf()) : ParamStorage {

    override fun store(param: DomainModel): DomainModel {
        storage.add(param.toStorageModel())
        return param
    }

    override fun retrieve(): DomainModel {
        return storage.last().toDomain()
    }
}

private fun StorageModel.toDomain(): DomainModel {
    return DomainModel(param)
}

private fun DomainModel.toStorageModel(): StorageModel {
    return StorageModel(domainParam)
}

class StorageModel(internal val param: String)

```
I am using here an in memory list of objects, and to simplify things adapter is in the same file. It should be replaced with some other storage implementation.

Remember to update the `settings.gradle.kts` with this module as well:
```kotlin
include(
    "bom",
    "application",
    "infrastructure:spring",
    "infrastructure:frontend",
    "infrastructure:dba",
)
```

# Summary
In this tutorial, I showed how to configure Gradle, keeping the versions in one place. The Bill of Material module that helps to manage the transitive dependencies forcing the use of a specific version, or rejecting some that are known to be vulnerable.

The above domain logic implementation is not exhausting the topic, it’s just scrub. There are some classes that I skipped for clarity, as they are just, ex. Spring configuration. For the full working example source code (with tests), please refer to the [GitHub](https://github.com/Augustyn/template-project/tree/v1).
Please feel free to submit a pull request or give a comment, if there's something that you feel that could be done better.
