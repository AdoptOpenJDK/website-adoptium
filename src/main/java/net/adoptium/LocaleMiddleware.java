package net.adoptium;

import io.quarkus.runtime.configuration.LocaleConverter;
import io.quarkus.vertx.web.RouteFilter;
import io.vertx.core.http.Cookie;
import io.vertx.ext.web.RoutingContext;
import net.adoptium.config.ApplicationConfig;
import net.adoptium.model.HeaderTemplate;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import java.util.List;
import java.util.Locale;

import static io.quarkus.qute.i18n.MessageBundles.ATTRIBUTE_LOCALE;

/**
 * Middleware run for all requests.
 */
public class LocaleMiddleware {
    private static final Logger LOG = Logger.getLogger(LocaleMiddleware.class);

    /**
     * Defines how long the locale cookie should be stored in the client's browser.
     * If no concrete time-span were defined, browsers would delete the cookie when the client session ends.
     */
    private static final long LOCALE_COOKIE_LIFETIME = 60 * 60 * 24 * 360L;

    @Inject
    ApplicationConfig appConfig;

    /**
     * Determines the language to use for the client when rendering.
     * <p>
     * By default the 'Accept-Language' header is parsed, but if a `locale` cookie is set, it will be preferred.
     * The value of this cookie can be overridden by setting the `locale` query parameter (this is done by the
     * 'change language' dropdown).
     * The cookie is always updated to reflect the latest language choice.
     * <p>
     * Since Qute considers the 'Accept-Language' header during rendering, we always override it to match the value we
     * determined. Additionally, an instance of {@link HeaderTemplate} is passed to future request handlers which
     * is already configured with all locale related fields it needs and ready to be rendered.
     *
     * @param rc current RoutineContext
     */
    @RouteFilter
    public void localeMiddleware(RoutingContext rc) {
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
                    // for now we don't include country specific locales since we'd then need to define a fallback from
                    // en-GB to en-US to en. Eg: doc files with the extension index_en.adoc need to be served for en-GB
                    // locales as well), which isn't implemented yet
                    acceptLanguage = parsedHeaderLocale.getLanguage();
                    localeCookie = Cookie.cookie(ATTRIBUTE_LOCALE, acceptLanguage);
                }
            }
        }

        HeaderTemplate header = new HeaderTemplate(appConfig.getLocales(), localeCookie.getValue());
        rc.put("header", header);

        localeCookie.setPath("/");
        localeCookie.setHttpOnly(true);
        localeCookie.setMaxAge(LOCALE_COOKIE_LIFETIME);
        rc.response().addCookie(localeCookie);
        rc.request().headers().set("Accept-Language", localeCookie.getValue());
        rc.next();
    }
}
