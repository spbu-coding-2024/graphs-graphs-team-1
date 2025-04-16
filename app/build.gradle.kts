import org.jetbrains.kotlin.ir.backend.js.compile

plugins {
    kotlin("jvm") version "1.9.20"
    id("org.jetbrains.compose")
    jacoco
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
    implementation("org.neo4j.driver:neo4j-java-driver:5.6.0")
    testImplementation("io.mockk:mockk:1.13.10")
    implementation(kotlin("stdlib"))
}

tasks.test {
    useJUnitPlatform()
finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        csv.required = false
        xml.required = false
        html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
    }
}