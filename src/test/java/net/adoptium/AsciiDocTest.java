package net.adoptium;

import com.microsoft.playwright.*;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

@QuarkusTest
public class AsciiDocTest {

    @Test
    public void AsciiDocDirectoryCorrect() {

        try (Playwright playwright = Playwright.create()) {
            List<BrowserType> browserTypes = Arrays.asList(
                    playwright.chromium(),
                    playwright.webkit(),
                    playwright.firefox()
            );
            for (BrowserType browserType : browserTypes) {
                try (Browser browser = browserType.launch()) {
                    BrowserContext context = browser.newContext();
                    Page page = context.newPage();
                    page.navigate("http://localhost:8081/docs/");
                    System.out.println(page.url());
                }
            }
        }
    }
}
