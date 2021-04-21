package net.adoptium;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import net.adoptium.api.ApiService;
import net.adoptopenjdk.api.v3.models.*;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

// index.html in META-INF.resources is used as static resource (not template)
@Path("/")
public class IndexResource {
    public static final int RECOMMENDED_JAVA_VERSION = 11;

    @Inject
    public IndexResource(Template index, @RestClient ApiService api) {
        this.index = index;
        this.api = api;
    }

    private final Template index;

    private final ApiService api;

    /**
     * getUserDownload queries the openjdk-api-v3 to find all suitable releases
     *
     * @param os
     * @param arch
     * @return
     */
    public Binary getUserDownload(OperatingSystem os, Architecture arch) {
        List<BinaryAssetView> availableReleaseList = api.getAvailableReleases(RECOMMENDED_JAVA_VERSION, JvmImpl.hotspot);
        Binary response = null;

        for (BinaryAssetView release : availableReleaseList) {
            if (release.getBinary().getHeap_size() != HeapSize.normal) continue;
            if (release.getBinary().getProject() != Project.jdk) continue;
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
        System.out.println("Binary: " + getUserDownload(user.getOs(), user.getArch()));
        Binary dl = getUserDownload(OperatingSystem.linux, Architecture.x64);
        System.err.println("index: " + index.instance());
        return index.data("version", dl.getScm_ref()).data("thank-you-version", dl.getScm_ref().split("_")[0]);
    }
}
