plugins {
    java
    application
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
}

tasks.test {
    useJUnitPlatform()
}
