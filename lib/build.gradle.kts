import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)
    testImplementation("org.assertj:assertj-core:3.26.3")

    // Selenium
    testImplementation("io.github.bonigarcia:selenium-jupiter:5.1.1")
    testImplementation("org.seleniumhq.selenium:selenium-java:3.141.59")
    testImplementation("io.github.bonigarcia:webdrivermanager:5.9.2")

    // REST Assured
    testImplementation("io.rest-assured:rest-assured:5.3.0")
    testImplementation("org.hamcrest:hamcrest:2.2")

    // Cucumber
    testImplementation("io.cucumber:cucumber-java:7.15.0")
    testImplementation("io.cucumber:cucumber-junit:7.15.0") // For JUnit 5 integration
    testImplementation("io.cucumber:cucumber-junit-platform-engine:latest.release")

    // Playwright
    testImplementation("com.microsoft.playwright:playwright:1.39.0")

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
    testLogging {
        showStandardStreams = false
        exceptionFormat = TestExceptionFormat.FULL
    }
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

// Rest Assured tests configuration
val apiTestSourceSet = sourceSets.create("apiTest") {
    java.srcDir("src/test/api")
    resources.srcDir("src/test/api/resources")
    compileClasspath += sourceSets["main"].output + sourceSets["test"].compileClasspath
    runtimeClasspath += output + sourceSets["test"].runtimeClasspath
}

// Rest Assured tests configuration
tasks.register<Test>("apiTest") {
    description = "Runs API tests."
    group = "verification"
    testClassesDirs = apiTestSourceSet.output.classesDirs
    classpath = apiTestSourceSet.runtimeClasspath
}

// Cucumber tests configuration
val cucumberTestSourceSet = sourceSets.create("cucumberTest") {
    java.srcDir("src/test/cucumber/java")
    resources.srcDir("src/test/cucumber/resources")
    compileClasspath += sourceSets["main"].output + sourceSets["test"].compileClasspath
    runtimeClasspath += output + sourceSets["test"].runtimeClasspath
}

// Cucumber tests configuration
tasks.register<Test>("cucumberTest") {
    description = "Runs Cucumber tests."
    group = "verification"
    testClassesDirs = cucumberTestSourceSet.output.classesDirs
    classpath = cucumberTestSourceSet.runtimeClasspath
    systemProperty("cucumber.glue", "org.example.steps") // Package for step definitions
    systemProperty("cucumber.features", "src/test/cucumber/resources/features") // Path to features
}
