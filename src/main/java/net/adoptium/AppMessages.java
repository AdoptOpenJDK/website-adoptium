package net.adoptium;

import io.quarkus.qute.i18n.Message;
import io.quarkus.qute.i18n.MessageBundle;
import io.quarkus.qute.i18n.MessageParam;
import org.jboss.resteasy.annotations.providers.jaxb.WrappedMap;

@MessageBundle
public interface AppMessages {

    // MessageParam:
    //@Message("Hello {name ?: 'world'}")
    @Message("Hello {name}")
    String hello_name(@MessageParam("name") String name);

    @Message("Hello")
    String hello();

    @Message("Download Java")
    String download_header();

}
