package net.adoptium;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import net.adoptium.api.ApiService;
import net.adoptium.exceptions.DownloadBinaryNotFoundException;
import net.adoptium.model.DownloadResourceHTMLData;
import net.adoptium.utils.DownloadArgumentGroup;
import net.adoptium.utils.DownloadStringArgumentExtractor;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.adoptopenjdk.api.v3.models.Binary;
import net.adoptopenjdk.api.v3.models.Release;

import static net.adoptium.utils.DownloadArgumentGroup.*;

@Path("/download")
public class DownloadResource {
    private static final Logger LOG = Logger.getLogger(DownloadResource.class);

    @Inject
    Template download;

    @Inject
    @RestClient
    ApiService api;

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("thank-you/{args}")
    public TemplateInstance get(@PathParam("args") String args) throws Exception {
        Map<DownloadArgumentGroup, String> versionDetails = DownloadStringArgumentExtractor.getVersionDetails(args);
        Binary binary = getBinary(versionDetails);
        DownloadResourceHTMLData htmlData = new DownloadResourceHTMLData(versionDetails, binary);
        TemplateInstance downloadPage = fillHTMLVariables(htmlData);
        return downloadPage;
    }

    private List<Release> requestDownloadVersion(Map<DownloadArgumentGroup, String> versionArguments) {
        return api.getRelease(versionArguments.get(VERSION),
                versionArguments.get(ARCH),
                versionArguments.get(HEAP_SIZE),
                versionArguments.get(IMAGE_TYPE),
                versionArguments.get(JVM_IMPL),
                versionArguments.get(OS),
                versionArguments.get(PROJECT),
                versionArguments.get(RELEASE_TYPE),
                versionArguments.get(VENDOR));
    }

    private Binary getBinary(Map<DownloadArgumentGroup, String> versionDetails) throws DownloadBinaryNotFoundException {
        List<Release> releaseList = requestDownloadVersion(versionDetails);
        List<Binary> binaryList = Arrays.asList(releaseList.get(0).getBinaries());
        if (binaryList.size() == 0) {
            throw new DownloadBinaryNotFoundException("Binary not found!", "Try to access this page from the root route.");
        } else if (binaryList.size() > 1) {
            //LOG.error("There are " + binaryList.size() + " binaries available for " + args + "! Expected just 1");
            LOG.error("There are " + binaryList.size() + " binaries available! Expected just 1");
        }
        return binaryList.get(0);
    }

    private TemplateInstance fillHTMLVariables(DownloadResourceHTMLData htmlData) {
        return download
                .data("downloadLink", htmlData.getDownloadLink())
                .data("imageType", htmlData.getImageType())
                .data("checksum", htmlData.getChecksum())
                .data("version", htmlData.getVersion())
                .data("vendor", htmlData.getVendor())
                .data("arch", htmlData.getArch())
                .data("os", htmlData.getOs());
    }
}