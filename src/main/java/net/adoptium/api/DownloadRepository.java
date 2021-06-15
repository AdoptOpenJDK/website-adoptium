package net.adoptium.api;

import net.adoptium.exceptions.DownloadBinaryNotFoundException;
import net.adoptium.model.Download;
import net.adoptium.utils.DownloadArgumentGroup;
import net.adoptopenjdk.api.v3.models.*;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.client.exception.ResteasyWebApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static net.adoptium.utils.DownloadArgumentGroup.*;

@ApplicationScoped
public class DownloadRepository {

    private static final Logger LOG = LoggerFactory.getLogger(DownloadRepository.class);

    public static final int RECOMMENDED_JAVA_VERSION = 11;

    public static final JvmImpl RECOMMENDED_JVM_IMPL = JvmImpl.hotspot;

    public static final ImageType RECOMMENDED_IMAGE_TYPE = ImageType.jdk;

    public static final Project RECOMMENDED_PROJECT = Project.jdk;

    public static final HeapSize RECOMMENDED_HEAP_SIZE = HeapSize.normal;

    public static final ReleaseType RECOMMENDED_RELEASE_TYPE = ReleaseType.ga;

    public static final Vendor RECOMMENDED_VENDOR = Vendor.adoptopenjdk;

    private final ApiService api;

    @Inject
    public DownloadRepository(@RestClient ApiService api) {
        this.api = api;
    }

    /**
     * Queries the openjdk-api-v3 to find all suitable releases
     *
     * @param os   Operating System on which the binary is to be run
     * @param arch Architecture on which the binary is to be run
     * @return a binary, preferably with installer, but this isn't available for all os/arch combinations. Nullable.
     */
    public Download getUserDownload(OperatingSystem os, Architecture arch) {
        List<BinaryAssetView> availableReleaseList = api.getAvailableReleases(
                RECOMMENDED_JAVA_VERSION, RECOMMENDED_JVM_IMPL);
        Download response = null;

        for (BinaryAssetView release : availableReleaseList) {
            if (!matchesRecommendations(release)) continue;
            if (release.getBinary().getOs() != os || release.getBinary().getArchitecture() != arch) continue;

            // if os & recommendations match, update response
            response = new Download(release.getBinary(), release.getVersion().getSemver());

            // if this binary even includes a real installer (which not all binaries have), immediately accept it
            if (release.getBinary().getInstaller() != null) break;
        }
        return response;
    }

    /**
     * Returns absolute path to thank-you URL with the specific args.
     *
     * @param download the Download to be executed
     * @return arg string
     */
    public String buildThankYouPath(Download download) {
        Binary binary = download.getBinary();
        return String.format("/download/thank-you/%s-%s-%s-%s-%s-%s-%s-%s-%s",
                binary.getOs(), binary.getArchitecture(), binary.getJvm_impl(),
                binary.getImage_type(), binary.getHeap_size(), binary.getProject(),
                RECOMMENDED_RELEASE_TYPE, RECOMMENDED_VENDOR, download.getSemver());
    }

    public List<Release> requestDownloadVersion(Map<DownloadArgumentGroup, String> versionArguments)
            throws DownloadBinaryNotFoundException {
        try {
            return api.getRelease(versionArguments.get(VERSION),
                    versionArguments.get(ARCH),
                    versionArguments.get(HEAP_SIZE),
                    versionArguments.get(IMAGE_TYPE),
                    versionArguments.get(JVM_IMPL),
                    versionArguments.get(OS),
                    versionArguments.get(PROJECT),
                    versionArguments.get(RELEASE_TYPE),
                    versionArguments.get(VENDOR));
        } catch (ResteasyWebApplicationException e) {
            throw new DownloadBinaryNotFoundException();
        }
    }

    /**
     * Returns the binary uniquely identified by the arguments supplied. The Map must contain a {@link DownloadArgumentGroup#VERSION}
     * all other fields are optional and help pin down a specific version.
     * <p>
     * There still is some uncertainty as to whether a combination of all DownloadArgumentGroup's uniquely identifies one
     * binary or if multiple ones with the same version data are possible, but since all of these binaries would satisfy
     * the caller's request, we can always safely return the first result.
     *
     * @param versionDetails Arguments to uniquely identify a binary
     * @return the binary identified by these arguments
     * @throws DownloadBinaryNotFoundException if the API does not have a binary with these meta-data
     */
    public Binary getBinary(Map<DownloadArgumentGroup, String> versionDetails) throws DownloadBinaryNotFoundException {
        List<Release> releases = requestDownloadVersion(versionDetails);
        if (releases.isEmpty()) throw new DownloadBinaryNotFoundException();
        Binary[] binaries = releases.get(0).getBinaries();

        if (binaries.length == 0) {
            throw new DownloadBinaryNotFoundException();
        } else if (binaries.length > 1) {
            LOG.error("There are {} binaries available! Expected just one. versionDetails: {}",  binaries.length, versionDetails);
        }
        return binaries[0];
    }

    private boolean matchesRecommendations(BinaryAssetView release) {
        Binary binary = release.getBinary();
        return binary.getJvm_impl() == RECOMMENDED_JVM_IMPL && binary.getImage_type() == RECOMMENDED_IMAGE_TYPE && binary.getProject() == RECOMMENDED_PROJECT &&
                binary.getHeap_size() == RECOMMENDED_HEAP_SIZE && release.getVendor() == RECOMMENDED_VENDOR;
    }
}
