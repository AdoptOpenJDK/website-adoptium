package net.adoptium;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateExtension;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.runtime.configuration.LocaleConverter;
import io.quarkus.vertx.web.RouteFilter;
import io.vertx.core.http.Cookie;
import io.vertx.ext.web.RoutingContext;
import net.adoptium.api.ApiService;
import net.adoptium.api.model.AvailableReleasesResponse;
import net.adoptium.config.ApplicationConfig;
import net.adoptium.model.Client;
import net.adoptium.model.Download;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static io.quarkus.qute.i18n.MessageBundles.ATTRIBUTE_LOCALE;

// index.html in META-INF.resources is used as static resource (not template)
@Path("/")
public class IndexResource {

    @Inject
    Template index;

    @Inject
    @RestClient
    ApiService api;

    @Inject
    ApplicationConfig appConfig;

    // TODO move to/use a library
    // inspired by https://github.com/AdoptOpenJDK/openjdk-website/blob/master/src/json/config.json
    private static final Map<Client, String> osDetection;

    static {
        osDetection = Map.of(
                new Client("windows", "x64"), "Windows Win Cygwin Windows Server 2008 R2 / 7 Windows Server 2008 / Vista Windows XP",
                new Client("linux", "x64"), "Linux Mint Debian Fedora FreeBSD Gentoo Haiku Kubuntu OpenBSD Red Hat RHEL SuSE Ubuntu Xubuntu hpwOS webOS Tizen",
                new Client("macos", "x64"), "Mac OS X OSX macOS Macintosh"
        );
    }

    private static final Logger LOG = Logger.getLogger(IndexResource.class);

    @RouteFilter
    void localeMiddleware(RoutingContext rc) {
        // @CookieParam(ATTRIBUTE_LOCALE) String locale
        // order: query, cookie, header
        // if query parameter `locale` is set, update cookie and Accept-Language header
        // qute uses the Accept-Language header
        String defaultLocale = appConfig.getDefaultLocale().getLanguage();
        Cookie localeCookie = rc.getCookie(ATTRIBUTE_LOCALE);

        List<String> localeQuery = rc.queryParam(ATTRIBUTE_LOCALE);
        if (localeQuery.size() > 0)
            localeCookie = Cookie.cookie(ATTRIBUTE_LOCALE, localeQuery.get(0));

        // if cookie & query-param aren't set, use
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
    public TemplateInstance get(@QueryParam("os") String clientOS, @QueryParam("arch") String clientArch, @HeaderParam("User-Agent") String userAgent, @HeaderParam("Accept-Language") String acceptLanguage) {
        // config (hard-coded, based on PO input)
        // usually hotspot but take the first one that exists for client os/arch
        String[] defaultPreference = new String[]{"hotspot", "openj9"};

        LOG.info("index - userAgent: " + userAgent);

        // get request to api based on headers
        // sort by our preference

        // TODO query api or use openjdk-api-v3-models
        String[] operatingSystems = new String[]{"windows", "linux"};
        String[] architectures = new String[]{"x64", "x32"};
        String[] jvmImpl = new String[]{"hotspot", "openj9"};

        // isNullOrBlank
        if (clientOS == null || clientOS.equals("")) {
            // TODO autodetect (possibly in middleware)
            clientOS = operatingSystems[0];
        }
        if (clientArch == null || clientArch.equals("")) {
            // TODO autodetect (possibly in middleware)
            clientArch = architectures[0];
        }

        AvailableReleasesResponse rel = api.getAvailableReleases();

        Integer[] suggestedVersions = new Integer[rel.getLtsReleases().length + 1];
        System.arraycopy(rel.getLtsReleases(), 0, suggestedVersions, 0, rel.getLtsReleases().length);
        suggestedVersions[rel.getLtsReleases().length] = rel.getMostRecentRelease();

        // TODO lots of work regarding vendor/project/etc. needs to be done here
        String finalClientOS = clientOS;
        String finalClientArch = clientArch;
        List<Download> downloads = Arrays.stream(suggestedVersions).map(v ->
                new Download("Java", "jdk", "" + v, "" + v, buildDownloadURL(v, "ga", finalClientOS, finalClientArch, "jdk", jvmImpl[0], "normal", "adoptopenjdk", "jdk"))
        ).collect(Collectors.toUnmodifiableList());

        return index.instance()
                .data("operatingSystems", operatingSystems)
                .data("architectures", architectures)
                .data("locales", appConfig.getLocales())
                .data("downloads", downloads)
                // user-specific
                .data("acceptLanguage", acceptLanguage) // only needed so that language select can compare the current value -- TODO: can't we read attributes.locale in html?
                .data("clientOS", clientOS)
                .data("clientArch", clientArch);
    }

    private String buildDownloadURL(int version, String rel, String os, String arch, String imageType, String jvmImpl, String heapSize, String vendor, String project) {
        String fmt = "https://api.adoptopenjdk.net/v3/installer/latest/%d/%s/%s/%s/%s/%s/%s/%s?project=%s";
        return String.format(fmt, version, rel, os, arch, imageType, jvmImpl, heapSize, vendor, project);
    }

    // TODO this is an ugly way of making the current locale known to the html
    // inspiration: https://github.com/quarkusio/quarkus/blob/main/extensions/qute/deployment/src/test/java/io/quarkus/qute/deployment/extensions/TemplateExtensionAttributeTest.java
    // does not return default attributes or allow @HeaderParam("Accept-Language")
    @TemplateExtension(namespace = "attr")
    static String locale(@TemplateExtension.TemplateAttribute(ATTRIBUTE_LOCALE) Object locale) {
        return "locale: " + locale;
    }
}

