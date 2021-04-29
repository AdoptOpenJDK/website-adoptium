package net.adoptium;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.quarkus.qute.CheckedTemplate;
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

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance download(DownloadResourceHTMLData htmlData);
    }

    @Inject
    public DownloadRepository repository;

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("thank-you/{args}")
    public TemplateInstance get(@PathParam("args") String args) {
        LOG.info("download/thank-you page called with args: " + args);
        Map<DownloadArgumentGroup, String> versionDetails = DownloadStringArgumentExtractor.getVersionDetails(args);
        Binary binary = repository.getBinary(versionDetails);
        DownloadResourceHTMLData htmlData = new DownloadResourceHTMLData(versionDetails, binary);
        return Templates.download(htmlData);
    }
}