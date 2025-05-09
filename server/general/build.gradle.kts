plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.hacksync"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

// Add system properties for TLS
System.setProperty("https.protocols", "TLSv1.2,TLSv1.3")
System.setProperty("javax.net.ssl.trustStoreType", "JKS")

repositories {
    mavenCentral()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
    gradlePluginPortal()
    maven { url = uri("https://packages.confluent.io/maven/") }
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://plugins.gradle.org/m2/") }
}

dependencies {
    implementation(libs.openapigen)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.swagger.ui)
    implementation(libs.ktor.openapi)
    implementation(libs.ktor.simple.cache)
    implementation(libs.ktor.simple.redis.cache)
    implementation(libs.ktor.server.rate.limiting)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.postgresql)
    implementation(libs.h2)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    implementation("io.insert-koin:koin-ktor:4.1.0-Beta5")
    implementation("io.insert-koin:koin-logger-slf4j:4.1.0-Beta5")
    implementation(libs.jdbi.core)
    implementation(libs.jdbi.kotlin)
    implementation(libs.jdbi.postgres)
    implementation(libs.jdbi.sql)
    implementation(libs.jbcrypt)
    implementation(libs.hikaricp)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
    testImplementation("io.mockk:mockk:1.13.7")
    testImplementation("io.insert-koin:koin-test:4.1.0-Beta5")
}
