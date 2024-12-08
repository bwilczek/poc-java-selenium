import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

class ToDoList {
    WebElement root;

    ToDoList(WebElement root) {
        this.root = root;
    }

    String getTitle() {
        return root.findElement(By.xpath("./div[@role='title']")).getText();
    }
}

class ToDoListsPage {
    WebDriver driver;
    SearchContext root;

    ToDoListsPage(WebDriver driver) {
        this.driver = driver;
        this.root = driver;
    }

    void open(String url) {
        driver.get(url);
    }

    List<ToDoList> getToDoLists() {
        List<WebElement> listRoots = root.findElements(By.xpath("//div[@role='todo_list']"));
        return listRoots.stream().map(element -> new ToDoList(element)).toList(); //.collect(Collectors.toList());
    }
}

class ChromeTest {
    WebDriver driver;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setupTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1400,800");
        driver = new ChromeDriver(options);
    }

    @AfterEach
    void teardown() {
        driver.quit();
    }

    @Test
    @DisplayName("List with title 'Home' is present by default")
    void testDefaultListNames() {
        ToDoListsPage page = new ToDoListsPage(driver);
        page.open("https://bwilczek.github.io/watir_pump_tutorial/todo_lists.html");
        List<ToDoList> todoLists = page.getToDoLists();
        List<String> titles = todoLists.stream().map(list -> list.getTitle()).toList(); // collect(Collectors.toList());

        assertThat(titles).contains("Home");
    }
}
