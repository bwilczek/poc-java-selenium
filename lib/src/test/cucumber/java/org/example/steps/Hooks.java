package org.example.steps;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;

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
