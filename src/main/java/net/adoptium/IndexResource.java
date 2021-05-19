package net.adoptium;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import net.adoptium.api.DownloadRepository;
import net.adoptium.config.ApplicationConfig;
import net.adoptium.model.Download;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

// index.html in META-INF.resources is used as static resource (not template)
@Path("/")
public class IndexResource {

    private static final Logger LOG = Logger.getLogger(IndexResource.class);

    @Inject
    public Template index;

    @Inject
    ApplicationConfig appConfig;

    private final DownloadRepository repository;

    @Inject
    public IndexResource(DownloadRepository repository) {
        this.repository = repository;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get(@QueryParam("name") String name, @HeaderParam("user-agent") String ua) {
        UserSystem user = UserAgentParser.getOsAndArch(ua);
        if (user.getOs() == null) {
            LOG.warnf("no OS detected for ua: %s, redirecting...", ua);
            // TODO redirect
        }
        Download recommended = repository.getUserDownload(user.getOs(), user.getArch());
        if (recommended == null) {
            LOG.warnf("no binary found for user: %s, redirecting...", user);
            // TODO redirect
        }
        String thankYouURL = repository.buildRedirectArgs(recommended);
        LOG.infof("user: %s -> [%s] binary: %s", user, thankYouURL, recommended);
        return index
                .data("download_name", recommended.getBinary().getProject().toString())
                .data("version", recommended.getSemver())
                .data("thank-you-version", thankYouURL)
                .data("os", recommended.getBinary().getOs())
                .data("arch", recommended.getBinary().getArchitecture());
    }
}
