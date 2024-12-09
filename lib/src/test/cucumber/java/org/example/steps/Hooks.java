package org.example.steps;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;

public class Hooks {

    // WARNING, make playwright static variables ThreadLocal before going parallel:
    // Use ThreadLocal for Playwright objects to avoid conflicts when running parallel scenarios:
    // private static final ThreadLocal<Page> threadLocalPage = new ThreadLocal<>();
    // public static Page getPage() {
    //     return threadLocalPage.get();
    // }
    // Set the thread-local instance in the @Before hook.
    private static Playwright playwright;
    private static Browser browser;
    private static Page page;
    private static BrowserContext browserContext;

    public static Page getPage() {
        return page;
    }

    @BeforeAll
    public static void beforeAll() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @AfterAll
    public static void afterAll() {
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

    @Before
    public void beforeEachScenario() {
        browserContext = browser.newContext();
        page = browserContext.newPage();
    }

    @After
    public void afterEachScenario() {
        browserContext.close();
    }
}
