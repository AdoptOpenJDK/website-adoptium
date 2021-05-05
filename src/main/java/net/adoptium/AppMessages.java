package net.adoptium;

import io.quarkus.qute.i18n.Message;
import io.quarkus.qute.i18n.MessageBundle;
import io.quarkus.qute.i18n.MessageParam;
import net.adoptopenjdk.api.v3.models.*;

@MessageBundle
public interface AppMessages {

    @Message("Adoptium")
    String adoptium();

    @Message("Temurin is a free to use runtime for the Java™ programming language. " +
            "If you need Java™, this is the download you are looking for.")
    String welcomeMainText();

    @Message("Download {project ?: 'Java'}")
    String welcomeDownloadButtonHeader(@MessageParam("project") Project project);

    @Message("{version} for {os} {arch}")
    String welcomeDownloadButtonBody(@MessageParam("version") String version,
                                     @MessageParam("os") OperatingSystem os,
                                     @MessageParam("arch") Architecture arch);


    @Message("We could not detect your OS automatically")
    String welcomeClientOsUndetected();

    @Message("We don't support  your platform right now")
    String welcomeClientOsUnsupported();

    @Message("View all releases")
    String welcomeDownloadButtonFallback();

    @Message("Thank you for downloading")
    String thankYouDownloadStarting();

    @Message("If the download does not start within 10 seconds, ")
    String thankYouDownloadTimeout();

    @Message("try again to download manually")
    String thankYouDownloadTimeoutClickAgain();

    @Message("{vendor} {imageType} {version} for {os} {arch}")
    String thankYouDownloadStartingLink(@MessageParam("vendor") Vendor vendor,
                                        @MessageParam("imageType") ImageType imageType,
                                        @MessageParam("version") String version,
                                        @MessageParam("os") OperatingSystem os,
                                        @MessageParam("arch") Architecture arch);

    @Message("Checksum")
    String literalChecksum();

    @Message("Copy")
    String literalCopy();
}
