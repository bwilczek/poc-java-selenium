The goal is to have four tasks:
* [x] run junit for unit tests `./gradlew unitTest`
* [x] run junit for Selenium tests `./gradlew seleniumTest`
* [ ] create a basic POM for Selenium tests
* [x] run Rest Assured tests, possibly also using junit
* [ ] run cucumber features with Playwright underneath (POM included)

Suggestions about runnint `cucumber` from chatgpt:

## Run cucumber

To run **Cucumber** features in a Gradle project, you need to integrate Cucumber with your test framework (e.g., JUnit 5). Below is an example configuration for adding a custom task to run Cucumber feature files.

---

### 1. Add Cucumber Dependencies
Update your dependencies to include Cucumber:

```kotlin
dependencies {
    testImplementation("io.cucumber:cucumber-java:7.15.0")
    testImplementation("io.cucumber:cucumber-junit:7.15.0") // For JUnit 5 integration
    testImplementation("io.cucumber:cucumber-spring:7.15.0") // Optional, if Spring is used
}
```

---

### 2. Directory Structure for Cucumber Features
Organize your project as follows:

```
src/
├── main/
│   └── kotlin/
├── test/
│   └── kotlin/
│       └── com/example/steps/
│           └── StepDefinitions.kt
│   └── resources/
│       └── features/
│           └── example.feature
```

- **StepDefinitions**: Contains the step definition classes (`StepDefinitions.kt`).
- **features**: Contains `.feature` files that describe the test scenarios.

---

### 3. Create a Custom `cucumberTest` Task

Add the following to your `build.gradle.kts`:

```kotlin
val cucumberTestSourceSet = sourceSets.create("cucumberTest") {
    java.srcDir("src/test/kotlin")
    resources.srcDir("src/test/resources")
    compileClasspath += sourceSets["main"].output + configurations["testImplementation"]
    runtimeClasspath += output + compileClasspath
}

configurations["cucumberTestImplementation"].extendsFrom(configurations["testImplementation"])

tasks.register<Test>("cucumberTest") {
    description = "Runs Cucumber feature tests."
    group = "verification"

    testClassesDirs = cucumberTestSourceSet.output.classesDirs
    classpath = cucumberTestSourceSet.runtimeClasspath

    systemProperty("cucumber.glue", "com.example.steps") // Package for step definitions
    systemProperty("cucumber.features", "src/test/resources/features") // Path to features

    useJUnitPlatform() // Ensures JUnit 5 is used with Cucumber
}
```

---

### 4. Example `StepDefinitions.kt`

```kotlin
package com.example.steps

import io.cucumber.java.en.Given
import io.cucumber.java.en.When
import io.cucumber.java.en.Then
import kotlin.test.assertEquals

class StepDefinitions {
    private var result: Int = 0

    @Given("a number {int}")
    fun givenNumber(number: Int) {
        result = number
    }

    @When("I add {int}")
    fun addNumber(number: Int) {
        result += number
    }

    @Then("the result should be {int}")
    fun verifyResult(expected: Int) {
        assertEquals(expected, result)
    }
}
```

---

### 5. Example `example.feature`

```gherkin
Feature: Addition

  Scenario: Add two numbers
    Given a number 5
    When I add 10
    Then the result should be 15
```

---

### 6. Running Cucumber Tests
Execute the custom task:

```bash
./gradlew cucumberTest
```

---

### How It Works
- **Source Set**: The `cucumberTest` source set uses `src/test/kotlin` for step definitions and `src/test/resources` for `.feature` files.
- **Cucumber System Properties**:
  - `cucumber.glue`: Specifies the package containing step definitions.
  - `cucumber.features`: Specifies the directory containing `.feature` files.
- **JUnit 5 Integration**: The `useJUnitPlatform()` ensures JUnit 5 is used for test execution.

Let me know if you need further refinements!
