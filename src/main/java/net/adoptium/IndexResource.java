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
        String[] osArch = getOsAndArch(userAgent);
        return index.data("userAgent", userAgent)
                .data("os", osArch[0])
                .data("arch", osArch[1]);
    }

    private String[] getOsAndArch(String userAgent){
        userAgent = userAgent.toLowerCase();
        String[] osArch = new String[2];
        if(userAgent.contains("windows")){
            osArch[0] = "windows";
        } else if(userAgent.contains("linux")){
            osArch[0] = "linux";
        }
        if(userAgent.contains("x64") || userAgent.contains("x86_64") || userAgent.contains("64")){
            osArch[1] = "x64";
        } else if(userAgent.contains("x86")){
            osArch[1] = "x86";
        }
        return osArch;
    }
}

