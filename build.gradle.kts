import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
    application
}

application {
    mainClass.set("team.credible.action.versioncleaner.system.AppKt")
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

}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = JvmTarget.JVM_11.toString()
        freeCompilerArgs = freeCompilerArgs + "-Xcontext-receivers"
    }
}
