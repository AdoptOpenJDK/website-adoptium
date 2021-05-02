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
    public List<Locale> locales;

    @Inject
    @ConfigProperty(name = "quarkus.default-locale")
    public Locale defaultLocale;

    public Locale getDefaultLocale() {
        return defaultLocale;
    }
}