package net.adoptium;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import net.adoptium.api.ApiService;
import net.adoptium.configuration.JacksonKotlinModule;
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
    public static final int RECOMMENDED_JAVA_VERSION = 11;

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
        LOG.infof("user: %s -> binary: %s", user, recommended);
        return index.data("version", recommended.getScm_ref()).data("thank-you-version", recommended.getScm_ref().split("_")[0]);
    }
}
