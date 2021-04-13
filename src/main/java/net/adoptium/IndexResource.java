package net.adoptium;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

// index.html in META-INF.resources is used as static resource (not template)
@Path("/")
public class IndexResource {

    @Inject
    Template index;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get(@QueryParam("name") String name) {
        return index.data("name", name);
    }

    @GET
    @Path("/useragent")
    @Produces(MediaType.TEXT_HTML)
    public Response testGettingUserSystemInformation(@HeaderParam("user-agent") String userAgent) {
        return Response.status(200)
                .entity("userAgent : " + userAgent)
                .build();

    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance testGettingUserSystemInformation2(@HeaderParam("user-agent") String userAgent) {
        return index.data("user-agent", userAgent);
    }
}

