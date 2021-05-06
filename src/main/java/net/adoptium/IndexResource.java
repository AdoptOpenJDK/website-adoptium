package net.adoptium;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.runtime.configuration.LocaleConverter;
import io.quarkus.vertx.web.RouteFilter;
import io.vertx.core.http.Cookie;
import io.vertx.ext.web.RoutingContext;
import net.adoptium.api.DownloadRepository;
import net.adoptium.config.ApplicationConfig;
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
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.quarkus.qute.i18n.MessageBundles.ATTRIBUTE_LOCALE;

// index.html in META-INF.resources is used as static resource (not template)
@Path("/")
public class IndexResource {

    private static final Logger LOG = Logger.getLogger(IndexResource.class);

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
        public static native TemplateInstance index(IndexTemplate template);
    }

    @Inject
    public IndexResource(DownloadRepository repository, ApplicationConfig appConfig) {
        this.repository = repository;
        this.appConfig = appConfig;
    }

    @RouteFilter
    public void localeMiddleware(RoutingContext rc) {
        // @CookieParam(ATTRIBUTE_LOCALE) String locale
        // order: query, cookie, header
        // if query parameter `locale` is set, update cookie and Accept-Language header
        // qute uses the Accept-Language header
        String defaultLocale = appConfig.getDefaultLocale().getLanguage();
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
                LOG.info("locale - using Accept-Language: " + acceptLanguage);
                Locale parsedHeaderLocale = new LocaleConverter().convert(acceptLanguage);
                if (parsedHeaderLocale == null) {
                    LOG.info("locale - bad Accept-Language, using default");
                    localeCookie = Cookie.cookie(ATTRIBUTE_LOCALE, defaultLocale);
                } else {
                    acceptLanguage = parsedHeaderLocale.getLanguage();
                    localeCookie = Cookie.cookie(ATTRIBUTE_LOCALE, acceptLanguage);
                }
            }
        }

        // qute did the Accept-Language parsing for us, but to set the correct cookie we need to parse it outselves
        // how does quarks/qute do it'
        localeCookie.setPath("/");
        localeCookie.setHttpOnly(true);
        // if not set it's deleted when the session sends
        // set it to 1 year?
        localeCookie.setMaxAge(60 * 60 * 24 * 360);
        LOG.info("locale - using: " + localeCookie.getValue());
        rc.response().addCookie(localeCookie);
        rc.request().headers().set("Accept-Language", localeCookie.getValue());
        rc.next();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get(@HeaderParam("user-agent") String userAgent) {
        IndexTemplate data = getImpl(userAgent);
        return Templates.index(data);
    }

    IndexTemplate getImpl(String userAgent) {
        UserSystem clientSystem = UserAgentParser.getOsAndArch(userAgent);
        if (clientSystem.getOs() == null) {
            LOG.warnf("no OS detected for userAgent: %s", userAgent);
            return new IndexTemplate(appConfig.getLocales());
        }

        Download recommended = repository.getUserDownload(clientSystem.getOs(), clientSystem.getArch());
        if (recommended == null) {
            LOG.warnf("no binary found for clientSystem: %s", clientSystem);
            return new IndexTemplate(appConfig.getLocales());
        }

        String thankYouPath = repository.buildThankYouPath(recommended);
        LOG.infof("user: %s -> [%s] binary: %s", clientSystem, thankYouPath, recommended);
        return new IndexTemplate(recommended, thankYouPath, appConfig.getLocales());
    }
}
