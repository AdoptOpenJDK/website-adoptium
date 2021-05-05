package net.adoptium.model;

import net.adoptium.utils.DownloadArgumentGroup;
import net.adoptopenjdk.api.v3.models.Binary;
import net.adoptopenjdk.api.v3.models.Installer;
import net.adoptopenjdk.api.v3.models.Package;

import java.util.Map;

import static net.adoptium.utils.DownloadArgumentGroup.*;

public class ThankYouTemplate {
    private final String downloadLink;
    private final String imageType;
    private final String checksum;
    private final String version;
    private final String vendor;
    private final String arch;
    private final String os;

    public ThankYouTemplate(Map<DownloadArgumentGroup, String> versionDetails, Binary binary) {
        imageType = versionDetails.get(IMAGE_TYPE);
        version = versionDetails.get(VERSION);
        vendor = versionDetails.get(VENDOR);
        arch = versionDetails.get(ARCH);
        os = versionDetails.get(OS);
        if (binary.getInstaller() != null) {
            Installer installer = binary.getInstaller();
            downloadLink = installer.getLink();
            checksum = installer.getChecksum();
        } else {
            Package pkg = binary.getPackage();
            downloadLink = pkg.getLink();
            checksum = pkg.getChecksum();
        }
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public String getImageType() {
        return imageType;
    }

    public String getChecksum() {
        return checksum;
    }

    public String getVersion() {
        return version;
    }

    public String getVendor() {
        return vendor;
    }

    public String getArch() {
        return arch;
    }

    public String getOs() {
        return os;
    }
}
