package net.adoptium;

import com.microsoft.playwright.*;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

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
                File dir = new File("src/docs/asciidoc");
                Object[] filesObjectArray = Arrays.stream(dir.listFiles()).filter(p -> p.isFile() == true).toArray();
                File[] filesInDir = Arrays.copyOf(filesObjectArray, filesObjectArray.length, File[].class);
                for (File file : filesInDir) {
                    try (Browser browser = browserType.launch()) {
                        BrowserContext context = browser.newContext();
                        Page page = context.newPage();
                        try {
                            page.navigate(("http://localhost:8081/docs/" + file.getName()).replaceFirst("[.][^.]+$", "") + ".html");
                        } catch (PlaywrightException e) {
                            fail("Failed because http://localhost:8081/docs/" + file.getName().replaceFirst("[.][^.]+$", "") + ".html does not exist", e);
                        }
                    }
                }
            }
        }
    }
}
