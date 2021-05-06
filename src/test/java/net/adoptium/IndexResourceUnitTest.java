package net.adoptium;

import io.quarkus.qute.HtmlEscaper;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.i18n.MessageBundles;
import io.quarkus.test.Mock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import net.adoptium.api.ApiMockServer;
import net.adoptium.api.ApiService;
import net.adoptium.api.DownloadRepository;
import net.adoptium.model.Download;
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
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import javax.swing.text.html.HTML;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * QuarkusTest annotation is required for RestClientBuilder to work
 */
@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IndexResourceUnitTest {

    //@InjectMock
    //DownloadRepository mockRepository;

    @Test
    public void testNoDownloadAvailable() {
        DownloadRepository mockRepository = Mockito.mock(DownloadRepository.class);

        // TODO return Download
        Mockito.when(mockRepository.getUserDownload(OperatingSystem.linux, Architecture.x64)).thenReturn(null);

        IndexResource index = new IndexResource(mockRepository);
        System.out.println("index: " + index);

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
        assertThat(got.render(), CoreMatchers.containsString(htmlEscaper.map(bundle.welcomeClientOsUnsupported(), null)));
    }
}
