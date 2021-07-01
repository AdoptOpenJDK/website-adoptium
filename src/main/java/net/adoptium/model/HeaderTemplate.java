package net.adoptium.model;

import java.util.List;
import java.util.Locale;

public class HeaderTemplate {

    private final List<Locale> locales;

    private final String locale;

    public HeaderTemplate(final List<Locale> locales, final String locale) {
        this.locales = locales;
        this.locale = locale;
    }

    public String getLocale() {
        return locale;
    }

    public List<Locale> getLocales() {
        return locales;
    }
}
