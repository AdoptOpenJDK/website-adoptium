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

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IndexResourceTest {

    OkHttpClient client = new OkHttpClient();

    @TestHTTPEndpoint(IndexResource.class)
    @TestHTTPResource
    URL indexUrl;

    @Test
    public void testIndexLocale() {
        // q: relative preference
        final Map<Locale, String[]> languages = new HashMap<>() {{
            put(Locale.ENGLISH, new String[]{"en", "en-US", "en-US,en", "en-GB,en;q=0.5,de;q=0.3"});
            // no q means q=1
            put(Locale.GERMAN, new String[]{"de", "de-CH", "de-CH,de", "de-DE,de;q=0.9,en;q=0.4"/*, "de,en-GB;q=0.9"*/});
        }};

        languages.forEach((locale, headers) -> {
            // MessageBundle > ResourceBundle as it also supports the values defined using @Message
            // MessageBundle gets Locale from default locale
            // ResourceBundle bundle = ResourceBundle.getBundle("messages/msg", locale);
            System.out.println("testing locale: " + locale);
            //Locale.setDefault(locale); // doesn't work?
            // , Localized.Literal.of(locale.getCountry())
            AppMessages bundle;
            if (locale == Locale.ENGLISH) {
                bundle = MessageBundles.get(AppMessages.class);
            } else {
                bundle = MessageBundles.get(AppMessages.class, Localized.Literal.of(locale.getLanguage()));
            }

            for (String header : headers) {
                try {
                    Request request = new Request.Builder()
                            .header("Accept-Language", header)
                            .header("User-Agent", "Windows")
                            .url(indexUrl)
                            .build();
                    Response response = client.newCall(request).execute();
                    ResponseBody body = response.body();

                    assert body != null;
                    Assertions.assertEquals(200, response.code(), "locale: " + locale + ", header: " + header);

                    // we need a constant string (no {variable} input)
                    Assertions.assertTrue(body.string().contains(bundle.main_text()), "locale: " + locale + ", header: " + header + ", main_text: " + bundle.main_text());
                } catch (IOException e) {
                    fail("locale: " + locale + ", header: " + header, e);
                }
            }
        });
    }

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
            Assertions.assertTrue(page.content().contains(bundle.download_starting()), "thank-you page does not display download_starting");
        } catch (PlaywrightException e) {
            fail("failed to launch browser " + browserType.name());
        }
    }
}
