package net.adoptium;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import net.adoptium.api.DownloadRepository;
import net.adoptium.model.DownloadResourceHTMLData;
import net.adoptium.utils.DownloadArgumentGroup;
import net.adoptium.utils.DownloadStringArgumentExtractor;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import java.util.Map;

import net.adoptopenjdk.api.v3.models.Binary;

@Path("/download")
public class DownloadResource {
    private static final Logger LOG = Logger.getLogger(DownloadResource.class);

    @Inject
    DownloadRepository repository;
    @Inject
    Template download;


    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("thank-you/{args}")
    public TemplateInstance get(@PathParam("args") String args) {
        LOG.info("download/thank-you page called with args: " + args);
        Map<DownloadArgumentGroup, String> versionDetails = DownloadStringArgumentExtractor.getVersionDetails(args);
        Binary binary = repository.getBinary(versionDetails);
        DownloadResourceHTMLData htmlData = new DownloadResourceHTMLData(versionDetails, binary);
        TemplateInstance downloadPage = fillHTMLVariables(htmlData);
        return downloadPage;
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