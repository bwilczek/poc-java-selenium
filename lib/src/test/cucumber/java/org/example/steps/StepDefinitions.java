package org.example.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class StepDefinitions {
    private int result = 0;

    @Given("a number {int}")
    public void givenNumber(int number) {
        result = number;
    }

    @When("I add {int}")
    public void addNumber(int number) {
        result += number;
    }

    @Then("the result should be {int}")
    public void verifyResult(int expected) {
        assertEquals(expected, result);
    }
}
