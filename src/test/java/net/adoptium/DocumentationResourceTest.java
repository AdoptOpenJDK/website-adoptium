package net.adoptium;

import com.microsoft.playwright.*;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class DocumentationResourceTest {

    private static final String existingDocPageName = "testdoc1";
    private static final String nonexistantDocPageName = "thisPageDoesntExist";

    @Test
    void testGetExistingDoc() {
        Playwright playwright = Playwright.create();

        // TODO use global config
        BrowserType browserType = playwright.firefox();

        try (Browser browser = browserType.launch()) {
            BrowserContext context = browser.newContext(new Browser.NewContextOptions().setLocale("en-US"));
            Page page = context.newPage();

            page.navigate("localhost:8181/documentation/" + existingDocPageName);

            assertEquals(existingDocPageName, page.title());

        } catch (PlaywrightException e) {
            fail("failed to launch browser " + browserType.name());
        }
    }

    @Test
    void testGetNonexistantDoc() {
        Playwright playwright = Playwright.create();

        // TODO use global config
        BrowserType browserType = playwright.firefox();

        try (Browser browser = browserType.launch()) {
            BrowserContext context = browser.newContext(new Browser.NewContextOptions().setLocale("en-US"));
            Page page = context.newPage();

            page.navigate("localhost:8181/documentation/" + nonexistantDocPageName);

            assertEquals(existingDocPageName, page.title());

        } catch (PlaywrightException e) {
            fail("failed to launch browser " + browserType.name());
        }
    }
}
