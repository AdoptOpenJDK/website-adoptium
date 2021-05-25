package net.adoptium;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.vertx.ext.web.RoutingContext;
import net.adoptium.api.DownloadRepository;
import net.adoptium.model.Download;
import net.adoptium.model.IndexTemplate;
import net.adoptium.model.UserSystem;
import net.adoptium.utils.UserAgentParser;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class IndexResource {
    private static final Logger LOG = Logger.getLogger(IndexResource.class);
    private final DownloadRepository repository;

    @Inject
    RoutingContext routingContext;

    /**
     * Checked Templates ensure type-safety in html templating.
     */
    @CheckedTemplate
    public static class Templates {

        private Templates() {
            throw new IllegalStateException("This is an utility class and should not get instantiated");
        }

        /**
         * The method name of a `static native TemplateInstance`
         * refers to the name of a .html file in templates/DownloadResource.
         *
         * @param template all data accessible by the template
         * @return a Template with values from template filled in
         */
        public static native TemplateInstance index(IndexTemplate template);
    }

    @Inject
    public IndexResource(DownloadRepository repository) {
        this.repository = repository;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get(@HeaderParam("user-agent") String userAgent,
                                @HeaderParam("accept-language") String acceptLanguage){
        IndexTemplate data = getImpl(userAgent);
        return Templates.index(data).data("header", routingContext.get("header"));
    }

    IndexTemplate getImpl(String userAgent) {
        UserSystem clientSystem = UserAgentParser.getOsAndArch(userAgent);
        if (clientSystem.getOs() == null) {
            LOG.warnf("no OS detected for userAgent: %s", userAgent);
            return new IndexTemplate();
        }

        Download recommended = repository.getUserDownload(clientSystem.getOs(), clientSystem.getArch());
        if (recommended == null) {
            LOG.warnf("no binary found for clientSystem: %s", clientSystem);
            return new IndexTemplate();
        }

        String thankYouPath = repository.buildThankYouPath(recommended);
        LOG.infof("user: %s -> [%s] binary: %s", clientSystem, thankYouPath, recommended);
        return new IndexTemplate(recommended, thankYouPath);
    }
}
