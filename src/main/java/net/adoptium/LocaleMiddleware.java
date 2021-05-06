package net.adoptium;

import io.quarkus.runtime.configuration.LocaleConverter;
import io.quarkus.vertx.web.RouteFilter;
import io.vertx.core.http.Cookie;
import io.vertx.ext.web.RoutingContext;
import net.adoptium.config.ApplicationConfig;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import java.util.List;
import java.util.Locale;

import static io.quarkus.qute.i18n.MessageBundles.ATTRIBUTE_LOCALE;

public class LocaleMiddleware {
    private static final Logger LOG = Logger.getLogger(LocaleMiddleware.class);

    @Inject
    ApplicationConfig appConfig;

    @RouteFilter
    public void localeMiddleware(RoutingContext rc) {
        // @CookieParam(ATTRIBUTE_LOCALE) String locale
        // order: query, cookie, header
        // if query parameter `locale` is set, update cookie and Accept-Language header
        // qute uses the Accept-Language header
        String defaultLocale = appConfig.getDefaultLocale().getLanguage();
        Cookie localeCookie = rc.getCookie(ATTRIBUTE_LOCALE);

        List<String> localeQuery = rc.queryParam(ATTRIBUTE_LOCALE);
        if (!localeQuery.isEmpty())
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
        rc.response().addCookie(localeCookie);
        rc.request().headers().set("Accept-Language", localeCookie.getValue());
        rc.next();
    }
}
