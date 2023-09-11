import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
    id("com.diffplug.spotless") version "6.21.0"
    id("org.gradle.maven-publish")
    application
}

application {
    mainClass.set("team.credible.action.versioncleaner.system.App")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    val versionKtor = "2.3.4"
    implementation("io.ktor:ktor-client-core:$versionKtor")
    implementation("io.ktor:ktor-client-apache5:$versionKtor")
    implementation("io.ktor:ktor-client-logging:$versionKtor")
    implementation("io.ktor:ktor-client-content-negotiation:$versionKtor")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$versionKtor")

    val versionKoin = "3.4.3"
    implementation("io.insert-koin:koin-core:$versionKoin")

    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("ch.qos.logback:logback-classic:1.2.9")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xcontext-receivers"
    }
}

spotless {
    kotlin {
        ktlint("0.50.0")
    }
}