package org.kie.integration;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;


import java.nio.file.Paths;
import java.util.regex.Pattern;


import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
@QuarkusIntegrationTest
public class SynantisiUITestIT {

    private static final String schedulePage = "http://localhost:8081/";
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(true)
                .setSlowMo(1000));
    }

    @AfterAll
    static void closeBrowser() {
        playwright.close();
    }

    @Test
    public void healthTest() {
        page.navigate(schedulePage);

        assertThat(page).hasTitle(Pattern.compile("Synantisi F2F scheduler"));

        page.locator("#getStartedButton").click();

        page.getByText("Add meeting").click();
        page.locator("#addMeetingSubmitButton").click();

        page.getByText("Add timeslot").click();
        page.locator("#addTimeslotSubmitButton").click();

        page.getByText("Add room").click();
        page.locator("#addRoomSubmitButton").click();

        page.getByText("Solve").click();

        assertThat(page.locator("//html/body/div[1]/div[3]/div[1]/table/tbody/tr/td/div/div/h5"))
                .containsText("Code like a boss");
    }

    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext();
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(false));
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        context.tracing().stop(new Tracing.StopOptions()
                .setPath(Paths.get("UItrace.zip")));
        context.close();
    }

}
