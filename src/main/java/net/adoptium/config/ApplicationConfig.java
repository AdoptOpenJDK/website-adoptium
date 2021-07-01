package net.adoptium.config;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Locale;

@ApplicationScoped
public class ApplicationConfig {

    private final List<Locale> locales;

    private final Locale defaultLocale;

    @Inject
    public ApplicationConfig(@ConfigProperty(name = "quarkus.locales") final List<Locale> locales,
                             @ConfigProperty(name = "quarkus.default-locale") final Locale defaultLocale) {
        this.locales = locales;
        this.defaultLocale = defaultLocale;
    }

    public List<Locale> getLocales() {
        return locales;
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }
}