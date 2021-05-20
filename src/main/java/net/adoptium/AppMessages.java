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

    @Message("We don't support your platform right now")
    String welcomeClientOsUnsupported();

    @Message("View all releases")
    String welcomeDownloadButtonFallback();

    @Message("Thank you for downloading")
    String thankYouDownloadStarting();

    @Message("If your download does not start within 10 seconds, <a href='{downloadLink}'>click here</a>")
    String thankYouDownloadHint(@MessageParam("downloadLink") String downloadLink);

    @Message("{vendor} {imageType} {version} for {os} {arch}")
    String thankYouDownloadStartingLink(@MessageParam("vendor") Vendor vendor,
                                        @MessageParam("imageType") ImageType imageType,
                                        @MessageParam("version") String version,
                                        @MessageParam("os") OperatingSystem os,
                                        @MessageParam("arch") Architecture arch);

    @Message("Download not found")
    String exceptionDownloadNotFound();

    @Message("Version not found")
    String exceptionVersionNotFound();

    @Message("Try visiting the <a href=\"/\">home page</a>")
    String exceptionGenericHint();

    @Message("Checksum")
    String literalChecksum();

    @Message("Copy")
    String literalCopy();

    @Message("Adoptium Header (eng)")
    String headerAdoptium();

    @Message("Adoptium Footer (eng)")
    String footerAdoptium();
}
