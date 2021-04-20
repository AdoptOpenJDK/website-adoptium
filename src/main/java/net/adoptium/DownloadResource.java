package net.adoptium;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import com.fasterxml.jackson.;

import net.adoptopenjdk.api.v3.models.*;


//import org.json.*;

@Path("/download")
public class DownloadResource {

    // os-arch-jvm_impl-image_type-heap_size-project-release_type-vendor-version
    private static final Pattern pattern = Pattern.compile("^(?<os>\\w*)-(?<arch>\\w*)-(?<jvmImpl>\\w*)-(?<imageType>\\w*)-(?<heapSize>\\w*)-(?<project>\\w*)-(?<releaseType>\\w*)-(?<vendor>\\w*)-(?<version>[^-]*)$", Pattern.CASE_INSENSITIVE); //[^-]

    //TODO: Ask andreas (https://github.com/quarkusio/quarkus/issues/7883)
    public class NotFoundException extends RuntimeException {
        public final String msg;
        public NotFoundException(String msg) {
            this.msg = msg;
        }
    }

    @ServerExceptionMapper
    public Response mapException(NotFoundException x) {
        System.out.println("I was in public Response mapException");
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
    @Path("thank-you/{file:(\\w*)-(\\w*)-(\\w*)-(\\w*)-(\\w*)-(\\w*)-(\\w*)-([^-]*)}") //[^-]
    public TemplateInstance get(@PathParam("file") String file) throws IOException, InterruptedException {
        //TODO: Ask if we first should check if file matches an existing version in the api. or if we directly should send the request to the api and let the api check the version
        System.out.println("I was in public TemplateInstance get");
        Matcher matcher = pattern.matcher(file);
        if (!matcher.find()) {
            throw new NotFoundException("version 11 not found!");
        }
        String os = matcher.group("os"),
                arch = matcher.group("arch"),
                jvmImpl = matcher.group("jvmImpl"),
                imageType = matcher.group("imageType"),
                heapSize = matcher.group("heapSize"),
                project = matcher.group("project"),
                releaseType = matcher.group("releaseType"),
                vendor = matcher.group("vendor"),
                version = matcher.group("version");
        //String installerType = BinaryType.valueOf(binaryType.toUpperCase(Locale.ROOT)).getType();
        String downloadLink = "https://google.com";
        String checksumAlgo = "sha512";
        String checksum = "sdklfjskj8z32w92kj3n19283zhjfw";
        String installerType = "installer";
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://staging-api.adoptopenjdk.net/v3/assets/version/" + version
                        + "?architecture=" + arch
                        + "&heap_size=" + heapSize
                        + "&image_type=" + imageType
                        + "&jvm_impl=" + jvmImpl
                        + "&os=" + os
                        + "&project=" + project
                        + "&release_type=" + releaseType
                        + "&vendor=" + vendor)
                ).build();
        System.out.println(request.uri());
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonResponse = mapper.readTree(response.body());
        //jsonResponse.get(1).get("binaries").
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