package net.adoptium.model;

import java.util.List;
import java.util.Locale;

public class IndexTemplate {
    private Download download;
    private final String redirectPath;
    private final List<Locale> locales;

    /**
     * if client os could not be detected, or no download for client platform is available
     */
    private boolean error = false;

    /**
     * Constructor for error case, with no os/arch or binary set.
     * The download button will always link to the full releases page instead of a specific download.
     */
    public IndexTemplate(List<Locale> locales) {
        this.locales = locales;
        this.error = true;
        // TODO define constants for paths (application.properties ?)
        this.redirectPath = "/releases";
    }

    /**
     * Constructor for default case, where a download binary for os/arch has been found.
     *
     * @param download     recommended downlaod for client
     * @param redirectPath absolut path to thank you page
     */
    public IndexTemplate(Download download, String redirectPath, List<Locale> locales) {
        this.download = download;
        this.redirectPath = redirectPath;
        this.locales = locales;
    }

    public Download getDownload() {
        return download;
    }

    public String getRedirectPath() {
        return redirectPath;
    }

    public List<Locale> getLocales() {
        return locales;
    }

    public boolean isError() {
        return error;
    }
}
