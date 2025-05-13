

plugins {
    kotlin("jvm") version "1.9.20"
    id("org.jetbrains.compose")
    `java-library`
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
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.20")
    implementation("org.neo4j.driver:neo4j-java-driver:5.6.0")

    implementation(kotlin("stdlib"))

    // https://mvnrepository.com/artifact/org.neo4j.test/neo4j-harness
    testImplementation("org.neo4j.test:neo4j-harness:2025.03.0")
//    // gephi toolkit
    implementation(files("lib/gephi-toolkit-0.10.0-all.jar"))
    // https://mvnrepository.com/artifact/org.jgrapht/jgrapht-core
    implementation("org.jgrapht:jgrapht-core:1.5.2")
    implementation("com.google.code.gson:gson:2.13.1")

}


tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        csv.required = false
        xml.required = false
        html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
    }
}


tasks.build {
    dependsOn("Load")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.register("Load") {
    val path = "lib/gephi-toolkit-0.10.0-all.jar"
    val sourceUrl = "https://github.com/gephi/gephi-toolkit/releases/download/v0.10.0/gephi-toolkit-0.10.0-all.jar"
    val libsDirectory = File("lib")
    val jarFile = File(path)

    if (!libsDirectory.exists())
        libsDirectory.mkdir()
    if (!jarFile.exists())
        download(sourceUrl, path)
}

fun download(url: String, path: String){
    val destinationFile = File(path)
    ant.invokeMethod("get", mapOf("src" to url, "dest" to destinationFile))
}


