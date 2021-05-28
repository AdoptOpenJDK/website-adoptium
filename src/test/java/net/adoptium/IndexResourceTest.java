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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.URL;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Testing the localization middleware requires a full HTTP client.
 * q: relative preference, no q means q=1
 */
@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IndexResourceTest {

    OkHttpClient client = new OkHttpClient();

    @TestHTTPEndpoint(IndexResource.class)
    @TestHTTPResource
    URL indexUrl;

    @ParameterizedTest
    @MethodSource
    void testIndexLocalization(String localisationValue, String testString) throws IOException {
        Request request = new Request.Builder()
                .header("Accept-Language", localisationValue)
                .header("User-Agent", "linux x64")
                .url(indexUrl)
                .build();
        Response response = client.newCall(request).execute();
        ResponseBody body = response.body();

        assertThat(body).isNotNull();
        assertThat(response.code()).isEqualTo(200);

        assertThat(body.string()).contains(testString);
    }

    /**
     * @return the arguments needed to test the method: testIndexLocalization
     */
    private static Stream<Arguments> testIndexLocalization() {
        return Stream.of(
                Arguments.of("en-GB,en;q=0.5,de;q=0.3", "Temurin is a free to use runtime"),
                Arguments.of("de-DE,de;q=0.9,en;q=0.4", "Temurin ist eine gratis zu benutzende Laufzeitumgebung"),
                Arguments.of("", "Temurin is a free to use runtime")
        );
    }

    /**
     * ensures /download/thank-you path is correctly used in buildThankYouPath.
     */
    @Test
    void testDownloadRedirect() {
        Playwright playwright = Playwright.create();

        // TODO use global config
        BrowserType browserType = playwright.firefox();
        try (Browser browser = browserType.launch()) {
            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    // any Windows
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0) AppleWebKit/537.36 " +
                            "(KHTML, like Gecko) Chrome/99.0.7113.93 Safari/537.36")
                    .setLocale("en-US")
            );
            Page page = context.newPage();
            page.navigate(indexUrl.toString());

            // follow download link
            page.click("a[id=\"download_btn\"]");

            AppMessages bundle = MessageBundles.get(AppMessages.class);

            assertThat(page.url()).overridingErrorMessage
                    ("not redirected to /thank-you/").contains("/thank-you/");
            assertThat(page.content()).overridingErrorMessage
                    ("thank-you page does not display download_starting")
                    .contains(bundle.thankYouDownloadStarting());
        } catch (PlaywrightException e) {
            fail("failed to launch browser " + browserType.name());
        }
    }
}
