package net.adoptium;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.vertx.ext.web.RoutingContext;
import net.adoptium.api.DownloadRepository;
import net.adoptium.model.DownloadErrorTemplate;
import net.adoptium.model.ThankYouTemplate;
import net.adoptium.utils.DownloadArgumentGroup;
import net.adoptium.utils.DownloadStringArgumentExtractor;
import net.adoptopenjdk.api.v3.models.Binary;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/download")
public class DownloadResource {

    private static final Logger LOG = LoggerFactory.getLogger(DownloadResource.class);

    private final DownloadRepository repository;

    private final RoutingContext routingContext;


    /**
     * Checked Templates ensure type-safety in html templating.
     */
    @CheckedTemplate
    public static class Templates {

        /**
         * The method name of a `static native TemplateInstance`
         * refers to the name of a .html file in templates/DownloadResource.
         *
         * @param template all data accessible by the template
         * @return a Template with values from template filled in
         */
        public static native TemplateInstance download(ThankYouTemplate template);

        public static native TemplateInstance error(DownloadErrorTemplate template);
    }

    @Inject
    public DownloadResource(final DownloadRepository repository, final RoutingContext routingContext) {
        this.repository = repository;
        this.routingContext = routingContext;
    }

    /**
     * After clicking the download button, users will be redirected to this page.
     *
     * @param args hyphenated string of different properties identifying a specific binary version
     * @return rendered ThankYouTemplate
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("thank-you/{args}")
    public TemplateInstance get(@PathParam("args") final String args) {
        final ThankYouTemplate template = getImpl(args);
        return Templates.download(template).data("header", routingContext.get("header"));
    }

    ThankYouTemplate getImpl(final String args) {
        LOG.info("/download/thank-you page called with args: {}", args);
        final Map<DownloadArgumentGroup, String> versionDetails = DownloadStringArgumentExtractor.getVersionDetails(args);
        final Binary binary = repository.getBinary(versionDetails);
        return new ThankYouTemplate(versionDetails, binary);
    }
}
