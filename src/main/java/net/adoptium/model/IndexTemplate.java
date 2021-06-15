package net.adoptium.model;

public class IndexTemplate {

    private final Download download;

    private final String redirectPath;

    /**
     * if client os could not be detected, or no download for client platform is available
     */
    private final boolean error;

    /**
     * Constructor for error case, with no os/arch or binary set.
     * The download button will always link to the full releases page instead of a specific download.
     */
    public IndexTemplate() {
        this.error = true;
        this.download = null;
        this.redirectPath = "/releases";
    }

    /**
     * Constructor for default case, where a download binary for os/arch has been found.
     *
     * @param download     recommended downlaod for client
     * @param redirectPath absolut path to thank you page
     */
    public IndexTemplate(final Download download, final String redirectPath) {
        this.error = false;
        this.download = download;
        this.redirectPath = redirectPath;
    }


    public Download getDownload() {
        return download;
    }

    public String getRedirectPath() {
        return redirectPath;
    }

    public boolean isError() {
        return error;
    }
}
