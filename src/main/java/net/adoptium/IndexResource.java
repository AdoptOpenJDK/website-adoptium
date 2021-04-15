package net.adoptium;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

// index.html in META-INF.resources is used as static resource (not template)
@Path("/")
public class IndexResource {

    @Inject
    Template index;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance testGettingUserSystemInformation2(@HeaderParam("user-agent") String userAgent) {
        String[] osArch = UserAgentParser.getOsAndArch(userAgent);
        return index.data("userAgent", userAgent)
                .data("os", osArch[0])
                .data("arch", osArch[1]);
    }


}

