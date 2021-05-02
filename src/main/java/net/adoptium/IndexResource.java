package net.adoptium;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.runtime.configuration.LocaleConverter;
import io.quarkus.vertx.web.RouteFilter;
import io.vertx.core.http.Cookie;
import io.vertx.ext.web.RoutingContext;
import net.adoptium.api.DownloadRepository;
import net.adoptium.config.ApplicationConfig;
import net.adoptium.model.Download;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.util.List;

import static io.quarkus.qute.i18n.MessageBundles.ATTRIBUTE_LOCALE;

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

    @RouteFilter
    void localeMiddleware(RoutingContext rc) {
        // @CookieParam(ATTRIBUTE_LOCALE) String locale
        // order: query, cookie, header
        // if query parameter `locale` is set, update cookie and Accept-Language header
        // qute uses the Accept-Language header
        String defaultLocale = appConfig.defaultLocale.getLanguage();
        Cookie localeCookie = rc.getCookie(ATTRIBUTE_LOCALE);

        List<String> localeQuery = rc.queryParam(ATTRIBUTE_LOCALE);
        if (localeQuery.size() > 0)
            localeCookie = Cookie.cookie(ATTRIBUTE_LOCALE, localeQuery.get(0));

        // if cookie & query-param aren't set, use header
        if (localeCookie == null || localeCookie.getValue().equals("")) {
            String acceptLanguage = rc.request().getHeader("Accept-Language");
            if (acceptLanguage == null) {
                LOG.info("locale - using default");
                localeCookie = Cookie.cookie(ATTRIBUTE_LOCALE, defaultLocale);
            } else {
                LOG.info("locale - using Accept-Default");
                acceptLanguage = new LocaleConverter().convert(acceptLanguage).getLanguage();
                localeCookie = Cookie.cookie(ATTRIBUTE_LOCALE, acceptLanguage);
            }
        }

        // qute did the Accept-Language parsing for us, but to set the correct cookie we need to parse it outselves
        // how does quarks/qute do it'
        LOG.info("locale - using: " + localeCookie.getValue());
        rc.response().addCookie(localeCookie);
        rc.request().headers().set("Accept-Language", localeCookie.getValue());
        rc.next();
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
