plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.flinters"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    mainClass.set("com.flinters.aggregator.AggregatorApp")
}

repositories {
    mavenCentral()
}

dependencies {
    // CSV streaming
    implementation("org.apache.commons:commons-csv:1.10.0")
    // CLI arguments
    implementation("info.picocli:picocli:4.7.5")
    // Logging
    implementation("org.slf4j:slf4j-api:2.0.9")
    runtimeOnly("ch.qos.logback:logback-classic:1.4.14")

    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.8.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    archiveBaseName.set("aggregator")
    archiveClassifier.set("")
    manifest {
        attributes["Main-Class"] = "com.flinters.aggregator.AggregatorApp"
    }
}
