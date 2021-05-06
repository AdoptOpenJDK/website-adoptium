package net.adoptium;

import io.quarkus.qute.HtmlEscaper;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.TemplateInstanceBase;
import io.quarkus.qute.i18n.MessageBundles;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import net.adoptium.api.DownloadRepository;
import net.adoptium.config.ApplicationConfig;
import net.adoptium.model.Download;
import net.adoptium.model.IndexTemplate;
import net.adoptopenjdk.api.v3.models.Package;
import net.adoptopenjdk.api.v3.models.*;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Can't use AppMessages without @QuarkusTest -> use the english strings manually
 */
public class IndexResourceUnitTest {

    @Test
    public void testNoDownloadAvailable() {
        DownloadRepository mockRepository = Mockito.mock(DownloadRepository.class);
        TestTemplateInstance testTemplate = new TestTemplateInstance();

        Download mockDownload = new Download(
                new Binary(new Package(
                        "name", "link", 1, "", "", 1, "", ""
                ), 1, new DateTime(new Date()), null, new Installer(
                        "name", "link", 1, "", "", 1, "", ""
                ), HeapSize.normal, OperatingSystem.linux, Architecture.x64, ImageType.jdk, JvmImpl.hotspot, Project.jdk),
                "1.0.0"
        );
        String mockThankYouPath = "/mock-thank-you-path";

        Mockito.when(mockRepository.getUserDownload(OperatingSystem.linux, Architecture.x64)).thenReturn(mockDownload);
        Mockito.when(mockRepository.buildThankYouPath(mockDownload)).thenReturn(mockThankYouPath);

        ApplicationConfig testConfig = new ApplicationConfig(List.of(Locale.ENGLISH), Locale.ENGLISH);
        IndexResource index = new IndexResource(mockRepository, testConfig);
        index.provider = new TemplateProvider<>(testTemplate::data);

        // Linux x64: download exists
        // welcomeMainText and errorText are mutually exclusive, if welcomeMainText is shown there was no error
        TemplateInstance got = index.get("linux x64");
        assertFalse(((IndexTemplate) ((TestTemplateInstance) got).getData()).isError());

        // Linux x32: OS detected, no download
        got = index.get("linux x32");
        assertTrue(((IndexTemplate) ((TestTemplateInstance) got).getData()).isError());

        // empty user agent: OS unknown
        got = index.get("");
        assertTrue(((IndexTemplate) ((TestTemplateInstance) got).getData()).isError());
    }
}
