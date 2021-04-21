package net.adoptium;

import com.microsoft.playwright.*;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
                File dir = new File("target/classes/META-INF/resources/docs");
                Object[] filesObjectArray = Arrays.stream(dir.listFiles()).filter(File::isFile).toArray();
                File[] filesInDir = Arrays.copyOf(filesObjectArray, filesObjectArray.length, File[].class);
                for (File file : filesInDir) {
                    try (Browser browser = browserType.launch()) {
                        BrowserContext context = browser.newContext();
                        Page page = context.newPage();
                        try {
                            page.navigate(("http://localhost:8081/docs/" + file.getName()));
                        } catch (PlaywrightException e) {
                            fail("Failed because http://localhost:8081/docs/" + file.getName() + "does not exist", e);
                        }
                    } catch (PlaywrightException e){
                        fail("failed to lauch browser");
                    }
                }
            }
        }
    }
}
