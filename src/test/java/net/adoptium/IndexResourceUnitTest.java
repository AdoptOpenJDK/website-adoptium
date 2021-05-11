package net.adoptium;

import net.adoptium.api.DownloadRepository;
import net.adoptium.config.ApplicationConfig;
import net.adoptium.model.Download;
import net.adoptium.model.IndexTemplate;
import net.adoptopenjdk.api.v3.models.*;
import net.adoptopenjdk.api.v3.models.Package;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Can't use AppMessages without @QuarkusTest -> use the english strings manually
 */
public class IndexResourceUnitTest {

    @Test
    void testNoDownloadAvailable() {
        DownloadRepository mockRepository = Mockito.mock(DownloadRepository.class);

        // OperatingSystem, Architecture, etc. don't matter
        Download mockDownload = new Download(new Binary(
                new Package("mock-package", "", 1, "", "", 1, "", ""),
                1, new DateTime(new Date()), null, null, HeapSize.normal, OperatingSystem.linux, Architecture.x64, ImageType.jdk, JvmImpl.hotspot, Project.jdk),
                "1.0.0"
        );
        String mockThankYouPath = "/mock-thank-you-path";

        // define which downloads exist
        Mockito.when(mockRepository.getUserDownload(OperatingSystem.linux, Architecture.x64)).thenReturn(mockDownload);
        Mockito.when(mockRepository.getUserDownload(OperatingSystem.windows, Architecture.x32)).thenReturn(mockDownload);

        Mockito.when(mockRepository.buildThankYouPath(mockDownload)).thenReturn(mockThankYouPath);

        ApplicationConfig testConfig = new ApplicationConfig(List.of(Locale.ENGLISH), Locale.ENGLISH);
        IndexResource index = new IndexResource(mockRepository, testConfig);

        // welcomeMainText and errorText are mutually exclusive, if welcomeMainText is shown there was no error
        IndexTemplate got = index.getImpl("linux x64");
        assertThat(got.isError()).overridingErrorMessage("Linux x64 should have downloads (no error)").isFalse();

        got = index.getImpl("windows x32");
        assertThat(got.isError()).overridingErrorMessage("Windows x32 should have downloads (no error)").isFalse();

        got = index.getImpl("linux x32");
        assertThat(got.isError()).overridingErrorMessage("Linux x32 should not have any downloads").isTrue();

        got = index.getImpl("");
        assertThat(got.isError()).overridingErrorMessage("Empty user agent should not be detected").isTrue();
    }
}
