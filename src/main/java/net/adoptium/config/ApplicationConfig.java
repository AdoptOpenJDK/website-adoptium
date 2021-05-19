package net.adoptium.config;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Locale;

@ApplicationScoped
public class ApplicationConfig {
    @Inject
    @ConfigProperty(name = "quarkus.locales")
    List<Locale> locales;

    @Inject
    @ConfigProperty(name = "quarkus.default-locale")
    Locale defaultLocale;

    public List<Locale> getLocales() {
        return locales;
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }
}