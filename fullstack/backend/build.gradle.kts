val ktorVersion = "2.0.3"
val kotlinVersion = "1.7.20-Beta"
val logback_version: String by project

plugins {
    application
    kotlin("jvm") version "1.7.20"
    id("io.ktor.plugin") version "2.1.3"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.20"
}

group = "com.CatPLC"
version = "0.5"
application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")
    implementation("io.ktor:ktor-server-compression:$ktorVersion")
    implementation("com.lordcodes.turtle:turtle:0.5.0")
    implementation("com.opencsv:opencsv:5.7.1")
    implementation("org.neo4j.driver:neo4j-java-driver:4.4.0")
    implementation("com.beust:klaxon:5.5")
    implementation("org.apache.commons:commons-csv:1.9.0")
    implementation("com.google.code.gson:gson:2.10")

    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")


}