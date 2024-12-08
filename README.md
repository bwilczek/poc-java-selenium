The goals of this PoC:
* [x] run junit for unit tests `./gradlew unitTest`
* [x] run junit for Selenium tests `./gradlew seleniumTest`
* [x] create a basic POM for Selenium tests
* [x] run Rest Assured tests, possibly also using `./gradlew apiTest`
* [ ] run cucumber features with Playwright underneath (POM included) `./gradlew cucumberTest`
* [ ] add GHA

Suggestions about runnint `cucumber` from chatgpt:

## Cucumber Hooks and StepDefinitions

In a JUnit 5 project with Cucumber, you can define **Cucumber Hooks** (like `@Before`, `@After`, `@BeforeAll`, and `@AfterAll`) in a **dedicated class** within your test package. These hooks allow you to execute code at specific points in the Cucumber test lifecycle.

Here’s how to integrate Cucumber hooks in a JUnit 5 project:

---

### 1. **Create a Hooks Class**
Define a separate class for hooks within your `src/test` directory. Annotate the methods with Cucumber hook annotations (`@Before`, `@After`, etc.).

```java
import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.AfterAll;

public class Hooks {

    @BeforeAll
    public static void beforeAll() {
        System.out.println("This runs once before all scenarios.");
    }

    @AfterAll
    public static void afterAll() {
        System.out.println("This runs once after all scenarios.");
    }

    @Before
    public void beforeEachScenario() {
        System.out.println("This runs before each scenario.");
    }

    @After
    public void afterEachScenario() {
        System.out.println("This runs after each scenario.");
    }
}
```

---

### 2. **Ensure Hooks Are in the Glue Path**
Cucumber requires all step definitions and hooks to be in the **glue path**. The glue path is specified when you configure the Cucumber runner. By default, the glue path is the same package (or sub-packages) as your step definitions.

For example, if your step definitions are in `com.example.steps`, place the `Hooks` class in `com.example.steps` or a sub-package like `com.example.steps.hooks`.

---

### 3. **Configure JUnit 5 with Cucumber**
Ensure your project has a Cucumber JUnit 5 configuration class. It might look like this:

```java
import io.cucumber.junit.platform.engine.Cucumber;

@Cucumber
public class CucumberTest {
    // This class serves as the entry point for Cucumber with JUnit 5.
}
```

This class:
- Uses the `@Cucumber` annotation to integrate Cucumber with JUnit 5.
- Ensures all glue code (step definitions and hooks) is scanned and executed.

---

### 4. **Annotations Available for Cucumber Hooks**
You can use these annotations to execute hooks at specific times:
- `@Before`: Runs before each scenario.
- `@After`: Runs after each scenario.
- `@BeforeAll`: Runs once before all scenarios (requires a static method).
- `@AfterAll`: Runs once after all scenarios (requires a static method).
- `@BeforeStep`: Runs before each step in a scenario.
- `@AfterStep`: Runs after each step in a scenario.

---

### 5. **Dependency Requirements**
Ensure your `build.gradle` includes the required dependencies for Cucumber with JUnit 5:

```kotlin
dependencies {
    testImplementation("io.cucumber:cucumber-java:7.14.0")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:7.14.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}
```

---

### Example Test Layout:
```
src/test/java/
├── com/example/
│   ├── steps/
│   │   ├── StepDefinitions.java
│   │   ├── Hooks.java
│   └── CucumberTest.java
```

This setup ensures hooks and step definitions are recognized and executed during your Cucumber tests.

## Shared Playwright instance

To create a shared Playwright browser instance in a Cucumber `Hooks` class and make it accessible to `StepDefinitions` methods, you can use dependency injection. This ensures that the browser instance is properly managed and shared across the steps of a scenario.

Here's how to implement it:

---

### 1. **Add Playwright Dependency**
Ensure you have the Playwright Java dependency in your `build.gradle` file:

```kotlin
dependencies {
    testImplementation("com.microsoft.playwright:playwright:1.39.0")
}
```

---

### 2. **Define the Hooks Class**
The `Hooks` class initializes and manages the Playwright browser instance. Use `@Before` and `@After` hooks to control the lifecycle.

```java
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import io.cucumber.java.Before;
import io.cucumber.java.After;

public class Hooks {

    private static Playwright playwright;
    private static Browser browser;
    private static Page page;

    @Before
    public void setUp() {
        // Initialize Playwright and create a browser instance
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false)); // Headless mode off for debugging
        page = browser.newPage();
    }

    @After
    public void tearDown() {
        // Close the browser and Playwright
        if (page != null) {
            page.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    public static Page getPage() {
        return page;
    }
}
```

---

### 3. **Access the Browser Instance in StepDefinitions**
In the `StepDefinitions` class, use the `Hooks.getPage()` method to access the shared `Page` instance:

```java
import com.microsoft.playwright.Page;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class StepDefinitions {

    private final Page page = Hooks.getPage(); // Access the shared page instance

    @Given("I navigate to {string}")
    public void iNavigateTo(String url) {
        page.navigate(url); // Use the shared page instance to navigate
    }

    @When("I click on the button with selector {string}")
    public void iClickOnTheButtonWithSelector(String selector) {
        page.locator(selector).click(); // Use the shared page instance to interact
    }

    @Then("The page title should be {string}")
    public void thePageTitleShouldBe(String expectedTitle) {
        String actualTitle = page.title();
        if (!actualTitle.equals(expectedTitle)) {
            throw new AssertionError("Expected title: " + expectedTitle + ", but got: " + actualTitle);
        }
    }
}
```

---

### 4. **Important Notes**
1. **Thread Safety**: Cucumber creates a new instance of the `StepDefinitions` class for each scenario, ensuring thread safety. However, if you run scenarios in parallel, ensure the `Hooks` class uses thread-local storage for the `Page` instance.

2. **Parallel Execution**:
   - Use `ThreadLocal` for Playwright objects to avoid conflicts when running parallel scenarios:
     ```java
     private static final ThreadLocal<Page> threadLocalPage = new ThreadLocal<>();

     public static Page getPage() {
         return threadLocalPage.get();
     }
     ```
     Set the thread-local instance in the `@Before` hook.

3. **Headless vs. Headed Mode**:
   - Use `setHeadless(true)` in `LaunchOptions` for faster execution unless debugging.

---

### Example Workflow
1. Cucumber initializes the `Hooks` class and calls the `@Before` hook to set up Playwright.
2. Steps in `StepDefinitions` use the shared `Page` instance from `Hooks`.
3. After the scenario, the `@After` hook ensures Playwright resources are cleaned up.

This setup ensures an efficient and reusable Playwright browser instance across your tests.

## GHA setup

To run `gradlew test` in a GitHub Actions workflow, you need to set up a workflow configuration file that installs the required Java environment, checks out your code, and executes the Gradle command. Here's how you can do it:

---

### 1. **Create a Workflow File**
Add a YAML file in the `.github/workflows` directory of your repository. For example, name it `gradle-tests.yml`.

---

### 2. **Sample Workflow Configuration**

```yaml
name: Java CI with Gradle

on:
  push:
    branches:
      - main
  pull_request:
    branches:
      - main

jobs:
  test:
    runs-on: ubuntu-latest

    steps:
      # Step 1: Check out the repository
      - name: Checkout repository
        uses: actions/checkout@v3

      # Step 2: Set up Java
      - name: Set up JDK
        uses: actions/setup-java@v3
        with:
          java-version: '17' # Specify your required Java version
          distribution: 'temurin' # Use Temurin as the JDK distribution
          cache: gradle # Enable Gradle caching

      # Step 3: Cache Gradle dependencies
      - name: Cache Gradle dependencies
        uses: actions/cache@v3
        with:
          path: ~/.gradle/caches
          key: gradle-${{ runner.os }}-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
          restore-keys: |
            gradle-${{ runner.os }}-

      # Step 4: Run Gradle tests
      - name: Run tests
        run: ./gradlew test
```

---

### 3. **Explanation of Steps**
- **Checkout Repository**: Uses the `actions/checkout` action to pull your repository's code.
- **Set Up Java**: Configures the required JDK version (e.g., Java 17). Update the version to match your project's requirements.
- **Cache Dependencies**: Caches Gradle dependencies to speed up subsequent builds. The cache key is based on the Gradle files to ensure updates invalidate the cache.
- **Run Tests**: Executes `./gradlew test` to run your tests.

---

### 4. **Optional Enhancements**
- **Specify Gradle Wrapper**: Ensure the `gradlew` script is included in your repository and marked executable:
  ```bash
  chmod +x gradlew
  git add gradlew
  git commit -m "Add gradlew script"
  ```

- **Matrix Testing**: Test against multiple Java versions:
  ```yaml
  jobs:
    test:
      runs-on: ubuntu-latest
      strategy:
        matrix:
          java-version: [11, 17]
      steps:
        - uses: actions/checkout@v3
        - uses: actions/setup-java@v3
          with:
            java-version: ${{ matrix.java-version }}
            distribution: 'temurin'
        - run: ./gradlew test
  ```

---

### 5. **Triggering the Workflow**
This workflow triggers on:
- Push events to the `main` branch.
- Pull requests targeting the `main` branch.

You can adjust the `on` configuration to match your branch or event requirements.

---

### 6. **Viewing Results**
- After the workflow runs, check the **Actions** tab in your GitHub repository.
- View the logs to see the output of `./gradlew test`, including any test failures or build issues.

This setup ensures that your Gradle tests are automatically executed on every push or pull request, integrating seamlessly with your CI pipeline.
