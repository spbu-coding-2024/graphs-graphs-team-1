import org.jetbrains.kotlin.ir.backend.js.compile

plugins {
    kotlin("jvm") version "1.9.20"
    id("org.jetbrains.compose")
}


repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation(compose.desktop.currentOs)
    testRuntimeOnly("org.junit.platform:junit-platform-suite-engine:1.8.2")
    testImplementation("org.junit.platform:junit-platform-suite:1.13.0-M2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.2")
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.20")
}

tasks.test {
    useJUnitPlatform()
}