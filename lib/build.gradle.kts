import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)
    testImplementation("io.github.bonigarcia:selenium-jupiter:5.1.1")
    testImplementation("org.assertj:assertj-core:3.26.3")
    testImplementation("org.seleniumhq.selenium:selenium-java:3.141.59")
    testImplementation("io.github.bonigarcia:webdrivermanager:5.9.2")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // This dependency is exported to consumers, that is to say found on their compile classpath.
    api(libs.commons.math3)

    // This dependency is used internally, and not exposed to consumers on their own compile classpath.
    implementation(libs.guava)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Unit tests configuration
val unitTestSourceSet = sourceSets.create("unitTest") {
    java.srcDir("src/test/unit/java")
    resources.srcDir("src/test/unit/resources")
    compileClasspath += sourceSets["main"].output + sourceSets["test"].compileClasspath
    runtimeClasspath += output + sourceSets["test"].runtimeClasspath
}

// Unit tests configuration
tasks.register<Test>("unitTest") {
    description = "Runs unit tests."
    group = "verification"
    testClassesDirs = unitTestSourceSet.output.classesDirs
    classpath = unitTestSourceSet.runtimeClasspath
}

// Selenium tests configuration
val seleniumTestSourceSet = sourceSets.create("seleniumTest") {
    java.srcDir("src/test/selenium")
    resources.srcDir("src/test/selenium/resources")
    compileClasspath += sourceSets["main"].output + sourceSets["test"].compileClasspath
    runtimeClasspath += output + sourceSets["test"].runtimeClasspath
}

// Selenium tests configuration
tasks.register<Test>("seleniumTest") {
    description = "Runs Selenium tests."
    group = "verification"
    testClassesDirs = seleniumTestSourceSet.output.classesDirs
    classpath = seleniumTestSourceSet.runtimeClasspath
}
