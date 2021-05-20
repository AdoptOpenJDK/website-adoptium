package net.adoptium;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import net.adoptium.api.DownloadRepository;
import net.adoptium.config.ApplicationConfig;
import net.adoptium.model.DownloadErrorTemplate;
import net.adoptium.model.HeaderTemplate;
import net.adoptium.model.ThankYouTemplate;
import net.adoptium.utils.DownloadArgumentGroup;
import net.adoptium.utils.DownloadStringArgumentExtractor;
import net.adoptopenjdk.api.v3.models.Binary;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/download")
public class DownloadResource {
    private static final Logger LOG = Logger.getLogger(DownloadResource.class);
    private final ApplicationConfig appConfig;
    private final DownloadRepository repository;

    /**
     * Checked Templates ensure type-safety in html templating.
     */
    @CheckedTemplate
    public static class Templates {
        /**
         * The method name of a `static native TemplateInstance` refers to the name of a .html file in templates/DownloadResource.
         *
         * @param template all data accessible by the template
         * @return a Template with values from template filled in
         */
        public static native TemplateInstance download(ThankYouTemplate template);

        public static native TemplateInstance error(DownloadErrorTemplate template);
    }

    @Inject
    public DownloadResource(DownloadRepository repository, ApplicationConfig appConfig) {
        this.repository = repository;
        this.appConfig = appConfig;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("thank-you/{args}")
    public TemplateInstance get(@PathParam("args") String args, @HeaderParam("accept-language") String acceptLanguage) {
        ThankYouTemplate template = getImpl(args);
        HeaderTemplate header = new HeaderTemplate(appConfig.getLocales(), acceptLanguage);
        return Templates.download(template).data("header", header);
    }

    ThankYouTemplate getImpl(String args) {
        LOG.info("/download/thank-you page called with args: " + args);
        Map<DownloadArgumentGroup, String> versionDetails = DownloadStringArgumentExtractor.getVersionDetails(args);
        Binary binary = repository.getBinary(versionDetails);
        return new ThankYouTemplate(versionDetails, binary);
    }
}