package org.kie.integration;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.microsoft.playwright.Download;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
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
                .setSlowMo(400));
    }

    @AfterAll
    static void closeBrowser() {
        playwright.close();

        File file = new File("schedule.json");
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void scheduleTest() throws IOException {
        page.navigate(schedulePage);
        page.setDefaultTimeout(10000);

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Get started!")).click();

        //check reset data
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Reset Schedule")).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Remove all Meetings, Timeslots and Rooms")).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(" Solve")).click();
        assertThat(page.locator("#scheduleByRoom > tbody > th")).not().isVisible();

        //add default meeting
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(" Add meeting")).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit new meeting")).click();
        assertThat(page.locator("#unassignedMeetings").getByText("Code Like a Boss")).isVisible();
        assertThat(page.locator("#unassignedMeetings").getByText("J. Carmack")).isVisible();
        assertThat(page.locator("#unassignedMeetings").getByText("Sam Altman")).isVisible();
        assertThat(page.locator("#unassignedMeetings").getByText("0")).isVisible();
        assertThat(page.locator(".fa-trash")).isVisible();
        assertThat(page.locator(".fa-pen")).isVisible();

        //add new meeting
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(" Add meeting")).click();
        page.getByLabel("Topic").click();
        page.getByLabel("Topic").fill("DevOps party");
        page.getByLabel("Speaker", new Page.GetByLabelOptions().setExact(true)).click();
        page.getByLabel("Speaker", new Page.GetByLabelOptions().setExact(true)).fill("R. Stallman");
        page.locator("#meeting_attendees").click();
        page.locator("#meeting_attendees").fill("T.Hicks, T. Cook");
        page.getByLabel("Priority").click();
        page.getByLabel("Priority").fill("10");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit new meeting")).click();
        assertThat(page.locator("#unassignedMeetings").getByText("J. Carmack")).isVisible();
        assertThat(page.locator("#unassignedMeetings").getByText("T.Hicks, T. Cook")).isVisible();
        assertThat(page.locator("#unassignedMeetings").getByText("DevOps party")).isVisible();
        assertThat(page.locator("#unassignedMeetings").getByText("10")).isVisible();

        //add timeslots
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(" Add timeslot")).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit new timeslot")).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(" Add timeslot")).click();
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Day of week")).selectOption("Tuesday");
        page.getByLabel("Start time").click();
        page.getByLabel("Start time").fill("09:30");
        page.getByLabel("End time").click();
        page.getByLabel("End time").fill("10:30");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit new timeslot")).click();
        //add rooms
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(" Add room")).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit new room")).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(" Add room")).click();
        page.getByLabel("Name").click();
        page.getByLabel("Name").fill("Room B");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit new room")).click();

        //share schedule
        Download download1 = page.waitForDownload(() -> {
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(" Share")).click();
        });
        download1.saveAs(Path.of("schedule.json"));
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Reset Schedule")).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Remove all Meetings, Timeslots and Rooms")).click();
        assertThat(page.locator("#unassignedMeetings")).isEmpty();

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(" Upload")).click();
        page.getByLabel("Choose file").click();
        page.getByLabel("Choose file").setInputFiles(Paths.get("schedule.json"));
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Upload schedule")).click();

        assertThat(page.locator("#unassignedMeetings").getByText("Code Like a Boss")).isVisible();
        assertThat(page.locator("#unassignedMeetings").getByText("DevOps party")).isVisible();

        //edit meeting
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("")).first().click();
        page.locator("#edit_meeting_priority").click();
        page.locator("#edit_meeting_priority").fill("10");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Update meeting")).click();
        assertThat(page.getByText("Code like a bossby J. Carmack10Sam Altman")).isVisible();

        page.locator("#unassignedMeetings").getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("")).nth(1).click();
        assertThat(page.locator("#unassignedMeetings").getByText("DevOps party")).not().isVisible();

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(" Add meeting")).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit new meeting")).click();

        //calculated state
        //solve and check that meetings are in different rooms in the same timeslot
        solve();
        assertThat(page.locator("#timeslot9room11")).containsText("Code like a boss");
        assertThat(page.locator("#timeslot9room12")).containsText("DevOps party");

        //adding new meetings moves it to unassigned
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(" Add meeting")).click();
        page.getByLabel("Topic").click();
        page.getByLabel("Topic").fill("Code like a Boss Unassigned");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit new meeting")).click();
        assertThat(page.locator("#unassignedMeetings").getByText("Code Like a Boss Unassigned")).isVisible();
        page.locator("#unassignedMeetings small.fa-trash").click();

        //check edit assigned meetings moves it to unassigned
        page.locator("#timeslot9room12 small.fa-pen").click();
        page.locator("#edit_meeting_priority").click();
        page.locator("#edit_meeting_priority").fill("5");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Update meeting")).click();
        assertThat(page.locator("#unassignedMeetings")).containsText("DevOps party");


        //check reassignment of meeting if room gets deleted
        solve();
        page.getByRole(AriaRole.CELL, new Page.GetByRoleOptions().setName("Room B ")).getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("")).click();
        assertThat(page.locator("#reassignMeetingsOnRoom")).containsText("DevOps party");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Reassign meetings")).click();
        assertThat(page.locator("#unassignedMeetings").getByText("DevOps party")).isVisible();

        //check views (Check table header and slot)
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(" Add room")).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit new room")).click();
        solve();

        //speaker view
        page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName("By speaker")).click();
        assertThat(page.locator("#scheduleByTopic > thead > tr > th:nth-child(2) > span"))
                .containsText("J. Carmack");
        assertThat(page.locator("#timeslot9teacherSi4gQ2FybWFjaw")).containsText("J. Carmack");

        //attendees view
        page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName("By attendees")).click();
        assertThat(page.locator("#scheduleByAttendees > thead > tr > th:nth-child(2) > span"))
                .containsText("Sam Altman");
        assertThat(page.locator("#timeslot9teacherUi4gU3RhbGxtYW4")).containsText("R. Stallman");
    }

    private void solve() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(" Solve")).click();
        page.waitForSelector(":text('Score: 0hard/0medium/-16soft')", new Page.WaitForSelectorOptions().setTimeout(5000));
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(" Stop solving")).click();
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
