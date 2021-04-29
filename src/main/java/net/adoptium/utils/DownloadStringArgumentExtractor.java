package net.adoptium.utils;

import net.adoptium.exceptions.DownloadInvalidArgumentException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.adoptium.utils.DownloadArgumentGroup.*;

public class DownloadStringArgumentExtractor {

    /*
    This Pattern is used to extract the arguments from the path parameter {args} in the GET-method from {DownloadResource}.
    The pattern is not used to check if the arguments are valid. It checks if the string to match holds enough arguments
    to build the download-api-request and puts the found arguments into their specified groups.

    Groups:     Found in the enum {DownloadArgumentGroup}
    Patterns:   [^-\/] accept anything except the -, \ and the / symbol
                The - symbol outside of a group is used to distinguish between arguments and their groups.
    Group example: (?<os>[^-\/]*) -> accept anything except the specified symbols and put the found substring in the group os
    Group "version" is the only group that accepts - symbols.

    Some examples for which the pattern matches:
        os-arch-jvm_impl-image_type-heap_size-project-release_type-vendor-version
        windows-x64-hotspot-jdk-normal-jdk-ga-adoptopenjdk-11.0.10+9
    */
    private static final String REGEX_DOWNLOAD = "^(?<" + OS + ">[^-\\/]*)-" +
                                                "(?<" + ARCH + ">[^-\\/]*)-" +
                                                "(?<" + JVM_IMPL + ">[^-\\/]*)-" +
                                                "(?<" + IMAGE_TYPE + ">[^-\\/]*)-" +
                                                "(?<" + HEAP_SIZE + ">[^-\\/]*)-" +
                                                "(?<" + PROJECT + ">[^-\\/]*)-" +
                                                "(?<" + RELEASE_TYPE + ">[^-\\/]*)-" +
                                                "(?<" + VENDOR + ">[^-\\/]*)-" +
                                                "(?<" + VERSION + ">[^\\/]*)$";

    private static final Pattern DOWNLOAD_PATTERN = Pattern.compile(REGEX_DOWNLOAD, Pattern.CASE_INSENSITIVE);

    public static Map<DownloadArgumentGroup, String> getVersionDetails(String stringVersionArguments) throws DownloadInvalidArgumentException {
        Matcher matcher = DOWNLOAD_PATTERN.matcher(stringVersionArguments);
        if(!matcher.find()) {
            throw new DownloadInvalidArgumentException("Version not found!", "Try to access this page from the root route.");
        }
        Map<DownloadArgumentGroup, String> versionDetails = extractArgumentsToMap(matcher);
        return versionDetails;
    }

    private static Map<DownloadArgumentGroup, String> extractArgumentsToMap(Matcher matcher) {
        Map<DownloadArgumentGroup, String> versionDetails = new HashMap<>();
        for(DownloadArgumentGroup downloadArgGroup : DownloadArgumentGroup.values()) {
            String argument = matcher.group(downloadArgGroup.toString());
            versionDetails.put(downloadArgGroup, argument);
        }
        return versionDetails;
    }
}
