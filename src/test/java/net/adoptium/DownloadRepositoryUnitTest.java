package net.adoptium;

import net.adoptium.api.ApiService;
import net.adoptium.api.DownloadRepository;
import net.adoptium.exceptions.DownloadBinaryNotFoundException;
import net.adoptium.model.Download;
import net.adoptium.utils.DownloadArgumentGroup;
import net.adoptopenjdk.api.v3.models.Package;
import net.adoptopenjdk.api.v3.models.*;
import org.jboss.resteasy.client.exception.ResteasyWebApplicationException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.ws.rs.WebApplicationException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static net.adoptium.api.DownloadRepository.*;
import static net.adoptium.utils.DownloadArgumentGroup.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DownloadRepositoryUnitTest {

    @Test
    void testBuildThankYouPath() {
        Download mockDownload = new Download(new Binary(
                new Package("mock-package", "", 1, "", "", 1, "", ""),
                1, new DateTime(new Date()), null, null, HeapSize.normal, OperatingSystem.linux, Architecture.x64, ImageType.jdk, JvmImpl.hotspot, Project.jdk),
                "1.0.0"
        );

        // api not needed for this test
        DownloadRepository repository = new DownloadRepository(null);
        assertThat(repository.buildThankYouPath(mockDownload)).isEqualTo("/download/thank-you/linux-x64-hotspot-jdk-normal-jdk-" + RECOMMENDED_RELEASE_TYPE + "-" + RECOMMENDED_VENDOR + "-1.0.0");
    }

    // mock layer below: ApiService
    @Test
    void testGetUserDownload() {
        // first item has mismatched jvm_impl, should be skipped (recommended is hotspot, first is openj9)
        List<BinaryAssetView> availableReleases = List.of(
                new BinaryAssetView("bad-jmv_impl", Vendor.adoptopenjdk, new Binary(
                        new Package("mock-package", "", 1, "", "", 1, "", ""),
                        1, new DateTime(new Date()), null, null, RECOMMENDED_HEAP_SIZE, OperatingSystem.linux, Architecture.x64, RECOMMENDED_IMAGE_TYPE, JvmImpl.openj9, RECOMMENDED_PROJECT),
                        new VersionData(RECOMMENDED_JAVA_VERSION, 1, 1, null, null, 1, null, "1.0", "1.0.0", 1)
                ),
                new BinaryAssetView("good-release", Vendor.adoptopenjdk, new Binary(
                        new Package("mock-package-good", "", 1, "", "", 1, "", ""),
                        1, new DateTime(new Date()), null, null, HeapSize.normal, OperatingSystem.linux, Architecture.x64, ImageType.jdk, RECOMMENDED_JVM_IMPL, RECOMMENDED_PROJECT),
                        new VersionData(RECOMMENDED_JAVA_VERSION, 1, 1, null, null, 1, null, "1.0", "1.0.0", 1)
                )
        );
        BinaryAssetView expectedBAV = availableReleases.get(availableReleases.size() - 1);

        ApiService mockService = Mockito.mock(ApiService.class);
        Mockito.when(mockService.getAvailableReleases(RECOMMENDED_JAVA_VERSION, RECOMMENDED_JVM_IMPL))
                .thenReturn(availableReleases);

        DownloadRepository repository = new DownloadRepository(mockService);
        Download recommended = repository.getUserDownload(OperatingSystem.linux, Architecture.x64);

        Download expected = new Download(expectedBAV.getBinary(), expectedBAV.getVersion().getSemver());

        assertThat(recommended).isEqualTo(expected);
    }

    @Test
    void testGetBinary() {
        // first item has mismatched semver, should be skipped
        List<Release> availableReleases = List.of(
                new Release("id", ReleaseType.ga, "link", "name", new DateTime(new Date()), new DateTime(new Date()), new Binary[]{
                        new Binary(
                                new Package("mock-package", "", 1, "", "", 1, "", ""),
                                1, new DateTime(new Date()), null, null, RECOMMENDED_HEAP_SIZE, OperatingSystem.linux, Architecture.x64, RECOMMENDED_IMAGE_TYPE, JvmImpl.openj9, RECOMMENDED_PROJECT),
                }, 1, Vendor.adoptopenjdk,
                        new VersionData(1, 0, 0, null, null, 1, null, "1.0", "1.0.0", 1), null)
        );
        Release expectedRelease = availableReleases.get(availableReleases.size() - 1);

        Map<DownloadArgumentGroup, String> versionArguments = Map.of(VERSION, "1.0.0", ARCH, "x64", HEAP_SIZE, "normal", JVM_IMPL, "hotspot", IMAGE_TYPE, "jdk", OS, "linux", PROJECT, "jdk", RELEASE_TYPE, "ga", VENDOR, "adoptopenjdk");

        ApiService mockService = Mockito.mock(ApiService.class);
        Mockito.when(mockService.getRelease(versionArguments.get(VERSION), versionArguments.get(ARCH), versionArguments.get(HEAP_SIZE), versionArguments.get(IMAGE_TYPE), versionArguments.get(JVM_IMPL), versionArguments.get(OS), versionArguments.get(PROJECT), versionArguments.get(RELEASE_TYPE), versionArguments.get(VENDOR)))
                .thenReturn(availableReleases);

        DownloadRepository repository = new DownloadRepository(mockService);

        Binary got = repository.getBinary(versionArguments);

        assertThat(got).isEqualTo(expectedRelease.getBinaries()[0]);
    }

    @Test
    void testGetBinaryBadResponse() {
        List<Release> availableReleases = List.of(
                new Release("id", ReleaseType.ga, "link", "name", new DateTime(new Date()), new DateTime(new Date()), new Binary[]{}, 1, Vendor.adoptopenjdk,
                        new VersionData(1, 0, 0, null, null, 1, null, "1.0", "1.0.0", 1), null
                )
        );

        Map<DownloadArgumentGroup, String> versionArguments = Map.of(VERSION, "1.0.0", ARCH, "x64", HEAP_SIZE, "normal", JVM_IMPL, "hotspot", IMAGE_TYPE, "jdk", OS, "linux", PROJECT, "jdk", RELEASE_TYPE, "ga", VENDOR, "adoptopenjdk");

        ApiService mockService = Mockito.mock(ApiService.class);
        DownloadRepository repository = new DownloadRepository(mockService);

        // no binaries
        Mockito.when(mockService.getRelease(versionArguments.get(VERSION), versionArguments.get(ARCH), versionArguments.get(HEAP_SIZE), versionArguments.get(IMAGE_TYPE), versionArguments.get(JVM_IMPL), versionArguments.get(OS), versionArguments.get(PROJECT), versionArguments.get(RELEASE_TYPE), versionArguments.get(VENDOR)))
                .thenReturn(availableReleases);
        assertThatThrownBy(() -> repository.getBinary(versionArguments)).isExactlyInstanceOf(DownloadBinaryNotFoundException.class);

        // no releases
        Mockito.when(mockService.getRelease(versionArguments.get(VERSION), versionArguments.get(ARCH), versionArguments.get(HEAP_SIZE), versionArguments.get(IMAGE_TYPE), versionArguments.get(JVM_IMPL), versionArguments.get(OS), versionArguments.get(PROJECT), versionArguments.get(RELEASE_TYPE), versionArguments.get(VENDOR)))
                .thenReturn(List.of());
        assertThatThrownBy(() -> repository.getBinary(versionArguments)).isExactlyInstanceOf(DownloadBinaryNotFoundException.class);

        // resteasy request fails by throwing an exception
        Mockito.when(mockService.getRelease(versionArguments.get(VERSION), versionArguments.get(ARCH), versionArguments.get(HEAP_SIZE), versionArguments.get(IMAGE_TYPE), versionArguments.get(JVM_IMPL), versionArguments.get(OS), versionArguments.get(PROJECT), versionArguments.get(RELEASE_TYPE), versionArguments.get(VENDOR)))
                .thenThrow(new ResteasyWebApplicationException(new WebApplicationException("api server not available")));
        assertThatThrownBy(() -> repository.getBinary(versionArguments)).isExactlyInstanceOf(DownloadBinaryNotFoundException.class);
    }
}
