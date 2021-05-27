package net.adoptium;

import com.microsoft.playwright.*;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTest
public class DocumentationResourceTest {

    OkHttpClient client = new OkHttpClient();

    @TestHTTPEndpoint(DocumentationResource.class)
    @TestHTTPResource
    URL documentationURL;

    private static final String existingDocPageName = "testdoc1";

    @Test
    void testGetExistingDoc() {
        Playwright playwright = Playwright.create();

        // TODO use global config
        BrowserType browserType = playwright.firefox();

        try (Browser browser = browserType.launch()) {
            BrowserContext context = browser.newContext(new Browser.NewContextOptions().setLocale("en-US"));
            Page page = context.newPage();

            assertThat(page.title()).isEqualTo(existingDocPageName);

            assertEquals(existingDocPageName, page.title());
        } catch (PlaywrightException e) {
            fail("failed to launch browser " + browserType.name());
        }
    }

    @Test
    void testGetDocLocalized() {
        Playwright playwright = Playwright.create();

        // TODO use global config
        BrowserType browserType = playwright.firefox();

        try (Browser browser = browserType.launch()) {
            BrowserContext context = browser.newContext(new Browser.NewContextOptions().setLocale("de-CH"));
            Page page = context.newPage();

            page.navigate(documentationURL + "/" + existingDocPageName);

            assertThat(page.title()).isEqualTo(existingDocPageName);

            // explicit german text
            assertThat(page.content()).contains("DEUTSCH TESTDOC1");
        } catch (PlaywrightException e) {
            fail("failed to launch browser " + browserType.name());
        }
    }

    @Test
    void testDocumentationNotFoundException() throws IOException {
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(documentationURL.toString() + "/this-documentation-does-not-exist")
                .build();
        Response response = client.newCall(request).execute();
        ResponseBody body = response.body();

        assertThat(body).isNotNull();
        assertThat(response.code()).isEqualTo(404);

        // exceptionFileNotFound
        assertThat(body.string()).contains("Not found");
    }
}
