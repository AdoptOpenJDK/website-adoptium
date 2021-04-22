package net.adoptium;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import net.adoptium.api.ApiService;
import net.adoptopenjdk.api.v3.models.*;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

// index.html in META-INF.resources is used as static resource (not template)
@Path("/")
public class IndexResource {
    // TODO where to define these? custom struct?
    public static final int RECOMMENDED_JAVA_VERSION = 11;
    public static final HeapSize RECOMMENDED_HEAP_SIZE = HeapSize.normal;
    public static final ReleaseType RECOMMENDED_RELEASE_TYPE = ReleaseType.ga;
    public static final Vendor RECOMMENDED_VENDOR = Vendor.adoptopenjdk;

    private static final Logger LOG = Logger.getLogger(IndexResource.class);

    @Inject
    public IndexResource(@RestClient ApiService api) {
        this.api = api;
    }

    @Inject
    public Template index;

    private final ApiService api;

    /**
     * getUserDownload queries the openjdk-api-v3 to find all suitable releases
     *
     * @param os Operating System on which the binary is to be run
     * @param arch Architecture on which the binary is to be run
     * @return a binary, preferably with installer, but this isn't available for all os/arch combinations. Nullable.
     */
    public Binary getUserDownload(OperatingSystem os, Architecture arch) {
        List<BinaryAssetView> availableReleaseList = api.getAvailableReleases(RECOMMENDED_JAVA_VERSION, JvmImpl.hotspot);
        Binary response = null;

        for (BinaryAssetView release : availableReleaseList) {
            if (release.getBinary().getHeap_size() != HeapSize.normal) continue;
            if (release.getBinary().getProject() != Project.jdk) continue;
            if (release.getVendor() != Vendor.adoptopenjdk) continue;
            if (release.getBinary().getImage_type() != ImageType.jdk) continue;

            if (release.getBinary().getOs() == os && release.getBinary().getArchitecture() == arch) {
                response = release.getBinary();
                if (release.getBinary().getInstaller() != null) {
                    break;
                }
            }
        }
        return response;
    }

    private String buildThankYouURL(Binary binary) {
        // download URL does not like the _adopt suffix
        String version = binary.getScm_ref().split("_")[0].split("-")[1];
        System.out.println("vendor: " + Vendor.adoptopenjdk);
        return String.format("%s-%s-%s-%s-%s-%s-%s-%s-%s", binary.getOs(), binary.getArchitecture(), binary.getJvm_impl(), binary.getImage_type(), binary.getHeap_size(), binary.getProject(), RECOMMENDED_RELEASE_TYPE, RECOMMENDED_VENDOR, version);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get(@QueryParam("name") String name, @HeaderParam("user-agent") String ua) {
        UserSystem user = UserAgentParser.getOsAndArch(ua);
        if (user.getOs() == null) {
            LOG.warnf("no OS detected for ua: %s, redirecting...", ua);
            // TODO redirect :)
        }
        Binary recommended = getUserDownload(user.getOs(), user.getArch());
        if (recommended == null) {
            LOG.warnf("no binary found for user: %s, redirecting...", user);
            // TODO redirect
        }
        String thankYouURL = buildThankYouURL(recommended);
        LOG.infof("user: %s -> [%s] binary: %s", user, thankYouURL, recommended);
        return index
                .data("version", recommended.getScm_ref())
                .data("thank-you-version", thankYouURL)
                .data("os", recommended.getOs())
                .data("arch", recommended.getArchitecture());
    }
}
