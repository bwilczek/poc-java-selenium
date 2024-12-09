package org.example.steps;

import java.util.List;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

class ToDoListItem {

  private final Locator root;
  private final Locator labelName;
  private final Locator linkRemove;

  ToDoListItem(Locator root) {
      this.root = root;
      this.labelName = root.locator("xpath=./*[@role='name']");
      this.linkRemove = root.locator("xpath=./*[@role='rm']");
  }

  String name() {
      return labelName.textContent();
  }

  void delete() {
      linkRemove.click();
  }
}

class ToDoList {

  private final Locator root;
  private final Locator inputNewItem;
  private final Locator buttonAddItem;
  private final Locator labelTitle;

  ToDoList(Locator root) {
      this.root = root;
      this.inputNewItem = root.locator("xpath=./*[@role='new_item']");
      this.buttonAddItem = root.locator("xpath=./*[@role='add']");
      this.labelTitle = root.locator("xpath=./*[@role='title']");
  }

  public void addItem(String item) {
      inputNewItem.fill(item);
      buttonAddItem.click();
  }

  public String title() {
      return labelTitle.textContent();
  }

  public List<ToDoListItem> items() {
      return root.locator("li").all().stream().map(element -> new ToDoListItem(element)).toList();
  }
}

class ToDoListPage {

  private final Page page;

  ToDoListPage(Page page) {
      this.page = page;
  }

  void open() {
      page.navigate("https://bwilczek.github.io/watir_pump_tutorial/todo_lists.html");
  }

  ToDoList getList(String list) {
      // TODO: ask developer to replace the hacky @role attribute with a more suitable data-testid
      Locator node = page.locator(String.format("xpath=//*[text()='%s' and @role='title']/parent::*[@role='todo_list']", list));
      return new ToDoList(node);
  }
}
