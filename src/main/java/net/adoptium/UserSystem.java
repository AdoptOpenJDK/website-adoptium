package net.adoptium;

import net.adoptopenjdk.api.v3.models.Architecture;
import net.adoptopenjdk.api.v3.models.OperatingSystem;

public class UserSystem {
    private OperatingSystem os;
    private Architecture arch;

    public void UserSystem(OperatingSystem os, Architecture arch) {
        this.os = os;
        this.arch = arch;
    }

    public void setOs(OperatingSystem os) {
        this.os = os;
    }

    public void setArch(Architecture arch) {
        this.arch = arch;
    }

    public OperatingSystem getOs() {
        return os;
    }

    public Architecture getArch() {
        return arch;
    }
}
