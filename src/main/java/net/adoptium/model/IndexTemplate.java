package net.adoptium.model;

import io.quarkus.arc.Arc;
import net.adoptopenjdk.api.v3.models.Architecture;
import net.adoptopenjdk.api.v3.models.OperatingSystem;
import net.adoptopenjdk.api.v3.models.Project;

import java.util.Objects;

public class IndexTemplate {
    private OperatingSystem os = null;
    private Architecture arch = null;
    private String version = null;
    private Project project = null;
    private final String redirectPath;

    /**
     * if errorText is not null, an error has occured (eg. download not found)
     */
    private final String errorText;

    /**
     * Constructor for error case, with no os/arch or binary set.
     * The download button will always link to the full releases page instead of a specific download.
     *
     * @param errorText The text to display instead of a download version
     */
    public IndexTemplate(String errorText) {
        this.errorText = errorText;
        // TODO define constants for paths (application.properties ?)
        this.redirectPath = "/releases";
    }

    /**
     * Constructor for default case, where a download binary for os/arch has been found.
     *
     * @param os client os
     * @param arch client arch
     * @param version user visible version name of binary (should include major version)
     * @param project user visible name of the download
     * @param redirectPath absolut path to thank you page
     */
    public IndexTemplate(OperatingSystem os, Architecture arch, String version, Project project, String redirectPath) {
        this.os = os;
        this.arch = arch;
        this.version = version;
        this.project = project;
        this.redirectPath = redirectPath;
        this.errorText = null;
    }

    public OperatingSystem getOs() {
        return os;
    }

    public Architecture getArch() {
        return arch;
    }

    public String getVersion() {
        return version;
    }

    public Project getProject() {
        return project;
    }

    public String getRedirectPath() {
        return redirectPath;
    }

    public String getErrorText() {
        return errorText;
    }
}
