Feature: ToDo Lists

  Scenario: Add item to an existing list
    Given application page is open
    When I add item "Cheese" to list "Groceries"
    Then list "Groceries" should contain 4 items
