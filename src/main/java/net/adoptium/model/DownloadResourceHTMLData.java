package net.adoptium.model;

import net.adoptium.utils.DownloadArgumentGroup;
import net.adoptopenjdk.api.v3.models.Binary;
import net.adoptopenjdk.api.v3.models.Installer;
import net.adoptopenjdk.api.v3.models.Package;

import java.util.Map;

import static net.adoptium.utils.DownloadArgumentGroup.*;

public class DownloadResourceHTMLData {
    private String downloadLink;
    private String imageType;
    private String checksum;
    private String version;
    private String vendor;
    private String arch;
    private String os;

    public DownloadResourceHTMLData(Map<DownloadArgumentGroup, String> versionDetails, Binary binary) {
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

    public String getChecksum() {
        return checksum;
    }

    public String getImageType() {
        return imageType;
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
