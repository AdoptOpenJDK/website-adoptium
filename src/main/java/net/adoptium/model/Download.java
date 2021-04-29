package net.adoptium.model;

import net.adoptopenjdk.api.v3.models.Binary;

/**
 * A Download is identified by a binary and a version (semver)
 * This is what we effectively pass to /thank-you/{binary}
 */
public class Download {
    private final Binary binary;
    private final String semver;

    public Download(Binary binary, String semver) {
        this.binary = binary;
        this.semver = semver;
    }

    public Binary getBinary() {
        return binary;
    }

    public String getSemver() {
        return semver;
    }
}
