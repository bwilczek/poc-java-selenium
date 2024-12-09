package org.example.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static junit.framework.Assert.assertEquals;


public class StepDefinitions {

    ToDoListPage app = new ToDoListPage(Hooks.getPage());

    @Given("application page is open")
    public void applicationPageIsOpen() {
        app.open();
    }

    @When("I add item {string} to list {string}")
    public void addItemToList(String item, String list) {
        app.getList(list).addItem(item);
    }

    @Then("list {string} should contain {int} items")
    public void verifyItemCount(String listName, int itemsCount) {
        assertEquals(itemsCount, app.getList(listName).items().size());
    }
}
