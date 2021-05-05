package net.adoptium;

import io.quarkus.qute.i18n.Message;
import io.quarkus.qute.i18n.MessageBundle;
import io.quarkus.qute.i18n.MessageParam;
import net.adoptopenjdk.api.v3.models.Project;
import net.adoptopenjdk.api.v3.models.Vendor;
import net.adoptopenjdk.api.v3.models.OperatingSystem;
import net.adoptopenjdk.api.v3.models.Architecture;

@MessageBundle
public interface AppMessages {

    @Message("Adoptium")
    String adoptium();

    @Message("Temurin is a free to use runtime for the Java™ programming language. " +
            "If you need Java™, this is the download you are looking for.")
    String welcomeMainText();

    @Message("Download {project ?: 'Java'}")
    String welcomeDownloadButtonHeader(@MessageParam("project") Project project);

    @Message("{version}\n for {os} {arch}")
    String welcomeDownloadButtonBody(@MessageParam("version") String version,
                                       @MessageParam("os") OperatingSystem os,
                                       @MessageParam("arch") Architecture arch);

    @Message("Thank you for downloading")
    String thankYouDownloadStarting();

    @Message("If the download does not start within 10 seconds, ")
    String thankYouDownloadTimeout();

    @Message("try again to download manually")
    String thankYouDownloadTimoutClickAgain();

    @Message("Checksum")
    String thankYouChecksum();

    @Message("Copy")
    String literalCopy();

    @Message("{vendor} {imageType} {version} for {os} {arch}")
    String thankYouDownloadStartingLink(@MessageParam("vendor") Vendor vendor,
                                        @MessageParam("imageType") ImageType imageType,
                                        @MessageParam("version") String version,
                                        @MessageParam("os") OperatingSystem os,
                                        @MessageParam("arch") Architecture arch);

    @Message("We could not detect your OS")
    String client_os_undetected();

    @Message("We don't have something for you right now")
    String client_os_unsupported();

    @Message("View all releases")
    String download_button_fallback();
}
