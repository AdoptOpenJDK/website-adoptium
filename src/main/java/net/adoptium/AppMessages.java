package net.adoptium;

import io.quarkus.qute.i18n.Message;
import io.quarkus.qute.i18n.MessageBundle;
import io.quarkus.qute.i18n.MessageParam;

@MessageBundle
public interface AppMessages {

    @Message("adoptium")
    String adoptium();

    @Message("Download Java for free")
    String main_text();

    @Message("download {download_name ?: 'Java'}")
    String download_java(@MessageParam("download_name") String download_name);

    @Message("Download starting...")
    String download_starting();

    @Message("{version}\n for {os} {arch}")
    String download_version(@MessageParam("version") String version,
                            @MessageParam("os") String os,
                            @MessageParam("arch") String arch);
}
