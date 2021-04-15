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
    Template index;

    @Inject
    @RestClient
    ApiService api;

    private Binary getUserDownload(OperatingSystem os, Architecture arch) {
        List<BinaryAssetView> availableReleaseList = api.getAvailableReleases(RECOMMENDED_JAVA_VERSION, JvmImpl.hotspot);
        Binary response = null;

        for (BinaryAssetView release : availableReleaseList) {
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
        System.out.println("Binary: " + getUserDownload(OperatingSystem.windows, Architecture.x64));
        return index.data("name", name);
    }
}

