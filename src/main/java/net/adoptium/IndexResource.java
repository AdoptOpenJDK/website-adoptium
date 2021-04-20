package net.adoptium;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import net.adoptium.api.ApiService;
import net.adoptopenjdk.api.v3.models.*;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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

    Template index;

    ApiService api;

    /**
     * getUserDownload queries the openjdk-api-v3 to find all suitable releases
     *
     * @param os
     * @param arch
     * @return
     */
    Binary getUserDownload(OperatingSystem os, Architecture arch) {
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
    public TemplateInstance get(@QueryParam("name") String name) {
        Binary dl = getUserDownload(OperatingSystem.linux, Architecture.x64);
        return index.data("version", dl.getScm_ref()).data("thank-you-version", dl.getScm_ref().split("_")[0]);
    }
}

