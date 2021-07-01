package net.adoptium.model;

import net.adoptopenjdk.api.v3.models.Architecture;
import net.adoptopenjdk.api.v3.models.OperatingSystem;

public class UserSystem {

    private OperatingSystem os;

    private Architecture arch;

    public UserSystem(final OperatingSystem os, final Architecture arch) {
        this.os = os;
        this.arch = arch;
    }

    public void setOs(final OperatingSystem os) {
        this.os = os;
    }

    public void setArch(final Architecture arch) {
        this.arch = arch;
    }

    public OperatingSystem getOs() {
        return os;
    }

    public Architecture getArch() {
        return arch;
    }

    @Override
    public String toString() {
        return "UserSystem{" +
                "os=" + os +
                ", arch=" + arch +
                '}';
    }
}
