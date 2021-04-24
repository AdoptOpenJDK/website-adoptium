package net.adoptium;

import com.microsoft.playwright.*;
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

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IndexResourceTest {

    OkHttpClient client = new OkHttpClient();

    @TestHTTPEndpoint(IndexResource.class)
    @TestHTTPResource
    URL indexUrl;

    @Test
    public void testDownloadRedirect() {
        Playwright playwright = Playwright.create();

        // TODO use global config
        BrowserType browserType = playwright.firefox();
        try (Browser browser = browserType.launch()) {
            BrowserContext context = browser.newContext();
            Page page = context.newPage();
            page.navigate(indexUrl.toString());

            // follow download link
            page.click("a[id=\"download_btn\"]");

            AppMessages bundle = MessageBundles.get(AppMessages.class);

            Assertions.assertTrue(page.url().contains("/thank-you/"), "not redirected to /thank-you/");
            Assertions.assertTrue(page.content().contains(bundle.download_starting()), "thank-you page does not display download_starting");
        } catch (PlaywrightException e) {
            fail("failed to launch browser " + browserType.name());
        }
    }
}
