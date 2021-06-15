package net.adoptium;

import net.adoptium.api.DownloadRepository;
import net.adoptium.exceptions.DownloadInvalidArgumentException;
import net.adoptium.model.ThankYouTemplate;
import net.adoptium.utils.DownloadArgumentGroup;
import net.adoptium.utils.DownloadStringArgumentExtractor;
import net.adoptopenjdk.api.v3.models.Package;
import net.adoptopenjdk.api.v3.models.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DownloadResourceUnitTest {

    private final DownloadRepository mockRepository = Mockito.mock(DownloadRepository.class);

    @Test
    void testDownloadLink() {
        final String args = "windows-x64-hotspot-jdk-normal-jdk-ga-adoptopenjdk-11.0.10+9";

        // mockBinary is effectively ignored, as long as ThankYouTemplate is populated with the correct link
        // using args as downloadLink to ensure it's unique per test
        final Binary mockBinary = new Binary(new Package(
                "", args, 1, "", "", 1, "", ""
        ), 1, new DateTime(new Date()), null, null, HeapSize.normal, OperatingSystem.linux, Architecture.x64, ImageType.jdk, JvmImpl.hotspot, Project.jdk);

        // parse download arguments so we can mock getBinary with the correct parameters
        final Map<DownloadArgumentGroup, String> expectedVersionDetails = DownloadStringArgumentExtractor.getVersionDetails(args);
        Mockito.when(mockRepository.getBinary(expectedVersionDetails)).thenReturn(mockBinary);

        final DownloadResource download = new DownloadResource(mockRepository, null);

        final ThankYouTemplate got = download.getImpl(args);
        assertThat(got.getDownloadLink()).isEqualTo(args);
    }

    @Test
    void testArgParsingMissingArg() {
        final String args = "windows-x64-hotspot-jdk-jdk-ga-adoptopenjdk-11.0.10+9";
        final DownloadResource download = new DownloadResource(mockRepository, null);

        assertThatThrownBy(() -> download.getImpl(args)).isExactlyInstanceOf(DownloadInvalidArgumentException.class);
    }
}
