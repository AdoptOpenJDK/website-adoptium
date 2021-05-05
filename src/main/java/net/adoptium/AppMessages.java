package net.adoptium;

import io.quarkus.qute.i18n.Message;
import io.quarkus.qute.i18n.MessageBundle;
import io.quarkus.qute.i18n.MessageParam;
import net.adoptopenjdk.api.v3.models.Project;

@MessageBundle
public interface AppMessages {

    @Message("adoptium")
    String adoptium();

    @Message("Download Java for free")
    String main_text();

    @Message("download {download_name ?: 'Java'}")
    String download_java(@MessageParam("download_name") Project download_name);

    @Message("Download starting...")
    String download_starting();

    @Message("{version}\n for {os} {arch}")
    String download_version(@MessageParam("version") String version,
                            @MessageParam("os") String os,
                            @MessageParam("arch") String arch);

    @Message("We could not detect your OS")
    String client_os_undetected();

    @Message("We don't have something for you right now")
    String client_os_unsupported();

    @Message("View all releases")
    String download_button_fallback();
}
