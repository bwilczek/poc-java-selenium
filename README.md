The goal is to have four tasks:
* [x] run junit for unit tests `./gradlew unitTest`
* [x] run junit for Selenium tests `./gradlew seleniumTest`
* [ ] run Rest Assured tests, possibly also using junit
* [ ] run cucumber features with Playwright underneath

Suggestions from chatgpt:

## Tasks for unit and integration tests

To configure two tasks for running unit tests and integration tests separately, you can customize your `build.gradle.kts` files by defining the source sets and tasks specifically for each type of test. Below is how you can achieve this setup using JUnit 5:

### `build.gradle.kts` Configuration

```kotlin
plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Unit tests configuration
tasks.register<Test>("unitTest") {
    description = "Runs unit tests."
    group = "verification"
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    mustRunAfter(tasks.named("integrationTest")) // Optional: Defines execution order
}

// Integration tests configuration
val integrationTestSourceSet = sourceSets.create("integrationTest") {
    java.srcDir("src/integration/kotlin")
    resources.srcDir("src/integration/resources")
    compileClasspath += sourceSets["main"].output + configurations["testImplementation"]
    runtimeClasspath += output + compileClasspath
}

configurations["integrationTestImplementation"].extendsFrom(configurations["testImplementation"])

tasks.register<Test>("integrationTest") {
    description = "Runs integration tests."
    group = "verification"
    testClassesDirs = integrationTestSourceSet.output.classesDirs
    classpath = integrationTestSourceSet.runtimeClasspath
    mustRunAfter(tasks.named("test")) // Optional: Defines execution order
}
```

### Project Directory Structure
```
src/
├── main/
│   └── kotlin/
├── test/
│   └── kotlin/
│       └── com/example/tests/ExampleUnitTest.kt
└── integration/
    ├── kotlin/
    │   └── com/example/integration/ExampleIntegrationTest.kt
    └── resources/
```

### Explanation
1. **Unit Tests (`unitTest` task)**:
   - Use the default `test` source set (`src/test/kotlin`).
   - Standard JUnit 5 configuration applies here.
   - The task `unitTest` uses the default `test` classes and classpath.

2. **Integration Tests (`integrationTest` task)**:
   - A custom source set `integrationTest` is created with `src/integration/kotlin` as its source directory.
   - Its classpath includes the `main` output and test dependencies.
   - The `integrationTest` task executes tests in the `integrationTest` source set.

3. **Execution Order**:
   - `mustRunAfter` is optional but ensures a logical order if you invoke both tasks in one command.

### Running the Tasks
- **Run unit tests**:
  ```bash
  ./gradlew unitTest
  ```
- **Run integration tests**:
  ```bash
  ./gradlew integrationTest
  ```
- **Run all tests together**:
  ```bash
  ./gradlew unitTest integrationTest
  ```

This configuration separates unit and integration tests effectively while using JUnit 5 for both. Let me know if you'd like further customization!

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

## REST Assured

To define a task for running **API tests** using RestAssured and JUnit, you can set up a custom source set for API tests and a dedicated Gradle task. Below is an example configuration:

---

### 1. Add RestAssured and JUnit Dependencies
Add the required dependencies for RestAssured and JUnit:

```kotlin
dependencies {
    testImplementation("io.rest-assured:rest-assured:5.3.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.hamcrest:hamcrest:2.2")
}
```

---

### 2. Directory Structure for API Tests
Organize your project as follows:

```
src/
├── main/
│   └── kotlin/
├── test/
│   └── kotlin/
│       └── com/example/tests/
├── api/
│   └── kotlin/
│       └── com/example/api/
│           └── ApiTests.kt
└── api/resources/
```

- **`api/kotlin`**: Contains your API test classes.
- **`api/resources`**: Stores any resource files (e.g., test data, configuration).

---

### 3. Define API Test Source Set and Task

In your `build.gradle.kts`, configure the custom source set and task:

```kotlin
val apiTestSourceSet = sourceSets.create("apiTest") {
    java.srcDir("src/api/kotlin")
    resources.srcDir("src/api/resources")
    compileClasspath += sourceSets["main"].output + configurations["testImplementation"]
    runtimeClasspath += output + compileClasspath
}

configurations["apiTestImplementation"].extendsFrom(configurations["testImplementation"])

tasks.register<Test>("apiTest") {
    description = "Runs API tests using RestAssured and JUnit."
    group = "verification"

    testClassesDirs = apiTestSourceSet.output.classesDirs
    classpath = apiTestSourceSet.runtimeClasspath

    useJUnitPlatform() // Use JUnit 5
}
```

---

### 4. Example API Test Class

Create a test class in `src/api/kotlin/com/example/api/ApiTests.kt`:

```kotlin
package com.example.api

import io.restassured.RestAssured.given
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class ApiTests {

    @Test
    fun `should return correct user details`() {
        given()
            .baseUri("https://jsonplaceholder.typicode.com")
            .basePath("/users")
            .queryParam("id", 1)
        .`when`()
            .get()
        .then()
            .statusCode(200)
            .body("[0].name", equalTo("Leanne Graham"))
            .body("[0].username", equalTo("Bret"))
    }

    @Test
    fun `should return not found for invalid endpoint`() {
        given()
            .baseUri("https://jsonplaceholder.typicode.com")
        .`when`()
            .get("/invalid-endpoint")
        .then()
            .statusCode(404)
    }
}
```

---

### 5. Running the API Tests
Run the custom task with:

```bash
./gradlew apiTest
```

---

### Explanation
1. **Source Set**:
   - `apiTest` source set is configured for API test code and resources.
   - Includes `src/api/kotlin` and `src/api/resources`.

2. **API Test Task**:
   - The `apiTest` task is configured to execute only the API tests from the `apiTest` source set.
   - It uses the JUnit 5 platform (`useJUnitPlatform()`).

3. **Dependencies**:
   - RestAssured simplifies the HTTP request and response validation.
   - JUnit 5 is the test framework.

4. **Execution**:
   - You can run the `apiTest` task standalone or include it in a test suite with other tasks.

---

Let me know if you'd like to extend this further, such as integrating with a CI pipeline!
