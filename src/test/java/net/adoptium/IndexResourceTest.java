package net.adoptium;

import com.microsoft.playwright.*;
import io.quarkus.qute.i18n.Localized;
import io.quarkus.qute.i18n.MessageBundles;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Testing the localization middleware requires a full HTTP client.
 * q: relative preference, no q means q=1
 */
@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IndexResourceTest {

    OkHttpClient client = new OkHttpClient();

    @TestHTTPEndpoint(IndexResource.class)
    @TestHTTPResource
    URL indexUrl;

    @Test
    public void testIndexLocaleEn() throws IOException {
        Request request = new Request.Builder()
                .header("Accept-Language", "en-GB,en;q=0.5,de;q=0.3")
                .header("User-Agent", "linux x64")
                .url(indexUrl)
                .build();
        Response response = client.newCall(request).execute();
        ResponseBody body = response.body();

        assert body != null;
        Assertions.assertEquals(200, response.code());

        // we need a constant string (no {variable} input)
        // as suggested during code review: nothing dynamic :)
        Assertions.assertTrue(body.string().contains("Temurin is a free to use runtime"));
    }

    @Test
    public void testIndexLocaleDe() throws IOException {
        Request request = new Request.Builder()
                .header("Accept-Language", "de-DE,de;q=0.9,en;q=0.4")
                .header("User-Agent", "linux x64")
                .url(indexUrl)
                .build();
        Response response = client.newCall(request).execute();
        ResponseBody body = response.body();

        assert body != null;
        Assertions.assertEquals(200, response.code());

        Assertions.assertTrue(body.string().contains("Temurin ist eine gratis zu benutzende Laufzeitumgebung"));
    }

    @Test
    public void testIndexLocaleDefault() throws IOException {
        Request request = new Request.Builder()
                .header("Accept-Language", "")
                .header("User-Agent", "linux x64")
                .url(indexUrl)
                .build();
        Response response = client.newCall(request).execute();
        ResponseBody body = response.body();

        assert body != null;
        Assertions.assertEquals(200, response.code());

        // application.properties defines english as default
        Assertions.assertTrue(body.string().contains("Temurin is a free to use runtime"));
    }

    /**
     * ensures /download/thank-you path is correctly used in buildThankYouPath.
     */
    @Test
    public void testDownloadRedirect() {
        Playwright playwright = Playwright.create();

        // TODO use global config
        BrowserType browserType = playwright.firefox();
        try (Browser browser = browserType.launch()) {
            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    // any Windows
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.7113.93 Safari/537.36")
                    .setLocale("en-US")
            );
            Page page = context.newPage();
            page.navigate(indexUrl.toString());

            // follow download link
            page.click("a[id=\"download_btn\"]");

            AppMessages bundle = MessageBundles.get(AppMessages.class);

            Assertions.assertTrue(page.url().contains("/thank-you/"), "not redirected to /thank-you/");
            Assertions.assertTrue(page.content().contains(bundle.thankYouDownloadStarting()), "thank-you page does not display download_starting");
        } catch (PlaywrightException e) {
            fail("failed to launch browser " + browserType.name());
        }
    }
}
