package net.adoptium;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import net.adoptopenjdk.api.v3.models;

//import org.json.*;

@Path("/download/")
public class DownloadResource {

    private static final Pattern pattern = Pattern.compile("^(?<test>\\w*)-(\\w*)-(\\w*)-(\\w*)-(\\w*)-(\\w*)-(\\w*)$", Pattern.CASE_INSENSITIVE);

    public class NotFoundException extends RuntimeException {
        public final String msg;
        public NotFoundException(String msg) {
            this.msg = msg;
        }
    }


    @ServerExceptionMapper
    public Response mapException(NotFoundException x) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity("NOT FOUND!: " + x.msg)
                .build();
    }

    private enum BinaryType {
        INSTALLER("INSTALLER"),
        BINARY("BINARY");

        String type;
        BinaryType(String type) {
            this.type = type;
        }

        public String getType() {
            return this.type;
        }
    }

    @Inject
    Template download;

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/{file:(\\w*!)-(\\w*)-(\\w*)-(\\w*)-(\\w*)-(\\w*)-(\\w*)-(\\w*)}")
    public TemplateInstance get(@PathParam("file") String file) {
        Matcher matcher = pattern.matcher(file);
        if (matcher.find()) {
            System.out.println(matcher.group("test"));
        } else {
            throw new NotFoundException("version 11 not found!");
        }
        //String installerType = BinaryType.valueOf(binaryType.toUpperCase(Locale.ROOT)).getType();
        String downloadLink = "https://google.com";
        String checksumAlgo = "sha512";
        String checksum = "sdklfjskj8z32w92kj3n19283zhjfw";
        String imageType = "Temurin";
        String os = "ubuntu";
        String arch = "x86";
        String version = "11";
        String installerType = "installer";
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://api.adoptopenjdk.net/v3/assets/version/" + version))
                .setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
                .build();
        /*HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject obj = new JSONObject(response);
        String pageName = obj.getJSONObject("pageInfo").getString("pageName");*/
        return download
                .data("downloadLink", downloadLink)
                .data("imageType", imageType)
                .data("version", version)
                .data("os", os)
                .data("arch", arch)
                .data("installerType", installerType.toLowerCase(Locale.ROOT))
                .data("checksumAlgo", checksumAlgo)
                .data("checksum", checksum);
    }

}