package net.adoptium.model;

import net.adoptium.utils.DownloadArgumentGroup;
import net.adoptopenjdk.api.v3.models.Package;
import net.adoptopenjdk.api.v3.models.*;

import java.util.Map;

import static net.adoptium.utils.DownloadArgumentGroup.*;

public class ThankYouTemplate {
    private final String downloadLink;
    private final ImageType imageType;
    private final String checksum;
    private final String version;
    private final Vendor vendor;
    private final Architecture arch;
    private final OperatingSystem os;

    public ThankYouTemplate(Map<DownloadArgumentGroup, String> versionDetails, Binary binary) {
        imageType = ImageType.valueOf(versionDetails.get(IMAGE_TYPE));
        version = versionDetails.get(VERSION);
        vendor = Vendor.valueOf(versionDetails.get(VENDOR));
        arch = Architecture.forValue(versionDetails.get(ARCH));
        os = OperatingSystem.valueOf(versionDetails.get(OS));
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

    public ImageType getImageType() {
        return imageType;
    }

    public String getChecksum() {
        return checksum;
    }

    public String getVersion() {
        return version;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public Architecture getArch() {
        return arch;
    }

    public OperatingSystem getOs() {
        return os;
    }
}
