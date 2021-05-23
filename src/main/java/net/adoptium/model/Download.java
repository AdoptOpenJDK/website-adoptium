package net.adoptium.model;

import net.adoptopenjdk.api.v3.models.Binary;

import java.util.Objects;

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

    @Override
    public String toString() {
        return "Download{" +
                "binary=" + binary +
                ", semver='" + semver + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Download download = (Download) o;
        return binary.equals(download.binary) && semver.equals(download.semver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(binary, semver);
    }
}
