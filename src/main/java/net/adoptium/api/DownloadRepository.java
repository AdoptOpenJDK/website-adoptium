package net.adoptium.api;

import net.adoptium.exceptions.DownloadBinaryNotFoundException;
import net.adoptium.model.Download;
import net.adoptium.utils.DownloadArgumentGroup;
import net.adoptopenjdk.api.v3.models.*;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.exception.ResteasyWebApplicationException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static net.adoptium.utils.DownloadArgumentGroup.*;

@ApplicationScoped
public class DownloadRepository {
    private static final Logger LOG = Logger.getLogger(DownloadRepository.class);
    // TODO where to define these? custom struct?
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
     * getUserDownload queries the openjdk-api-v3 to find all suitable releases
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
            if (release.getBinary().getJvm_impl() != RECOMMENDED_JVM_IMPL) continue;
            if (release.getBinary().getImage_type() != RECOMMENDED_IMAGE_TYPE) continue;
            if (release.getBinary().getProject() != RECOMMENDED_PROJECT) continue;
            if (release.getBinary().getHeap_size() != RECOMMENDED_HEAP_SIZE) continue;
            if (release.getVendor() != RECOMMENDED_VENDOR) continue;

            if (release.getBinary().getOs() == os && release.getBinary().getArchitecture() == arch) {
                response = new Download(release.getBinary(), release.getVersion().getSemver());
                // if an installer is found, return here and don't search for other binaries
                if (release.getBinary().getInstaller() != null) {
                    return response;
                }
            }
        }
        return response;
    }

    /**
     * returns absolute path to thank-you URL with the specific args.
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

    // TODO i18n: only set app-messages key in error
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
            throw new DownloadBinaryNotFoundException("Binary not found!", "Try to access this page from the root route.");
        }
    }

    // TODO i18n: only set app-messages key in error
    public Binary getBinary(Map<DownloadArgumentGroup, String> versionDetails) throws DownloadBinaryNotFoundException {
        List<Release> releaseList = requestDownloadVersion(versionDetails);
        List<Binary> binaryList = Arrays.asList(releaseList.get(0).getBinaries());
        if (binaryList.isEmpty()) {
            throw new DownloadBinaryNotFoundException("Binary not found!", "Try to access this page from the root route.");
        } else if (binaryList.size() > 1) {
            LOG.error("There are " + binaryList.size() + " binaries available! Expected just 1.");
        }
        return binaryList.get(0);
    }

}
