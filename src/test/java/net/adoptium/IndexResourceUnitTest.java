package net.adoptium;

import io.quarkus.qute.HtmlEscaper;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.i18n.MessageBundles;
import io.quarkus.test.junit.QuarkusTest;
import net.adoptium.api.ApiMockServer;
import net.adoptium.api.ApiService;
import net.adoptium.api.DownloadRepository;
import net.adoptium.model.IndexTemplate;
import net.adoptopenjdk.api.v3.models.Architecture;
import net.adoptopenjdk.api.v3.models.OperatingSystem;
import net.adoptopenjdk.api.v3.models.Project;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockWebServer;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.swing.text.html.HTML;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * QuarkusTest annotation is required for RestClientBuilder to work
 */
@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IndexResourceUnitTest {

    /**
     * mockWebServer mocks the api by returning responses from json files in test/resources/api-stating
     */
    private MockWebServer mockWebServer;
    private ApiService remoteApi;

    @BeforeAll
    public void setupMockServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.setDispatcher(new ApiMockServer());
        mockWebServer.start();

        // all paths are registered as absolute paths
        HttpUrl baseUrl = mockWebServer.url("/");

        remoteApi = RestClientBuilder.newBuilder()
                .baseUri(baseUrl.uri())
                .build(ApiService.class);
    }

    @Test
    public void testNoDownloadAvailable() {
        DownloadRepository repository = new DownloadRepository(remoteApi);
        IndexResource index = new IndexResource(repository);

        AppMessages bundle = MessageBundles.get(AppMessages.class);

        // NOTE: to match the response, we need to escape our strings like qute does
        HtmlEscaper htmlEscaper = new HtmlEscaper();

        // Linux x64: download exists
        // welcomeMainText and errorText are mutually exclusive, if welcomeMainText is shown there was no error
        TemplateInstance got = index.get("linux x64");
        assertThat(got.render(), CoreMatchers.containsString(htmlEscaper.map(bundle.welcomeMainText(), null)));

        // Linux x32: OS detected, no download
        got = index.get("linux x32");
        assertThat(got.render(), CoreMatchers.containsString(htmlEscaper.map(bundle.welcomeClientOsUnsupported(), null)));

        // empty user agent: OS unknown
        got = index.get("");
        assertThat(got.render(), CoreMatchers.containsString(htmlEscaper.map(bundle.welcomeClientOsUndetected(), null)));
    }

    @AfterAll
    public void shutdownMockServer() throws IOException {
        mockWebServer.shutdown();
    }
}
