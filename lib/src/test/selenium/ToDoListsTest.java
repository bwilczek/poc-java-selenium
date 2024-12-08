import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
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
}

class ToDoListsPage {
    WebDriver driver;

    ToDoListsPage(WebDriver driver) {
        this.driver = driver;
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
        driver.get("https://bwilczek.github.io/watir_pump_tutorial/todo_lists.html");
        List<WebElement> lists = driver.findElements(By.xpath("//div[@role='todo_list']"));
        List<String> titles = lists.stream().map(element -> element.findElement(By.xpath("./div[@role='title']")).getText()).collect(Collectors.toList());

        assertThat(titles).contains("Home");
    }
}
