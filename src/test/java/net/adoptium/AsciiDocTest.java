package net.adoptium;

import com.microsoft.playwright.*;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTest
public class AsciiDocTest {
    //anderes file für test defaults -> schreiben wir rein die default browsertypes die wir benutzen
    @Test
    public void AsciiDocDirectoryCorrect() {
        try (Playwright playwright = Playwright.create()) {
            List<BrowserType> browserTypes = Arrays.asList(
                    playwright.chromium(),
                    playwright.webkit(),
                    playwright.firefox()
            );
            for (BrowserType browserType : browserTypes) {
                File dir = new File("target/classes/META-INF/resources/docs");
                Object[] filesObjectArray = Arrays.stream(Objects.requireNonNull(dir.listFiles())).filter(File::isFile).toArray();
                File[] filesInDir = Arrays.copyOf(filesObjectArray, filesObjectArray.length, File[].class);
                for (File file : filesInDir) {
                    try (Browser browser = browserType.launch()) {
                        BrowserContext context = browser.newContext();
                        Page page = context.newPage();
                        try {
                            page.navigate(("http://127.0.0.1:8081/docs/" + file.getName()));
                            System.out.println(browserType.name());
                        } catch (PlaywrightException e) {
                            System.out.println("I failed to navigate onto the website: " + browserType.name());
                            fail("Failed because http://127.0.0.1:8081/docs/" + file.getName() + " does not exist when opened with browser", e);
                        }
                    } catch (PlaywrightException e) {
                        fail("failed to launch browser " + browserType.name());
                    }
                }
            }
        }
    }
}
