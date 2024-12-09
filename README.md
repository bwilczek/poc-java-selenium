The goals of this PoC:
* [x] run junit for unit tests `./gradlew unitTest`
* [x] run junit for Selenium tests `./gradlew seleniumTest`
* [x] create a basic POM for Selenium tests
* [x] run Rest Assured tests, possibly also using `./gradlew apiTest`
* [x] run cucumber features with Playwright underneath (POM included) `./gradlew cucumberTest`
* [ ] add GHA

Suggestions about running `cucumber` from chatgpt:

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
