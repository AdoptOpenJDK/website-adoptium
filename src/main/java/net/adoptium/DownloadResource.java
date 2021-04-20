package net.adoptium;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import com.fasterxml.jackson.;


//import org.json.*;

@Path("/download")
public class DownloadResource {

    private static final String RELEASE_TYPE = "releaseType";
    private static final String IMAGE_TYPE = "imageType";
    private static final String HEAP_SIZE = "heapSize";
    private static final String JVM_IMPL = "jvmImpl";
    private static final String PROJECT = "project";
    private static final String VERSION = "version";
    private static final String VENDOR = "vendor";
    private static final String ARCH = "arch";
    private static final String OS = "os";
    /*
    This Pattern is used to extract the details from the path parameter {file} in the GET-method
    to download the wanted version.

    Groups:  os, arch, jvm_impl, image_type, heap_size, project, release_type, vendor, version
    Pattern: [^-] accept anything except the - symbol
    Group example: (?<os>[^-]*) -> accept anything except the - symbol and put the found substring in the Group os

    The pattern matches for a string like:
        os-arch-jvm_impl-image_type-heap_size-project-release_type-vendor-version
        windows-x64-hotspot-jdk-normal-jdk-ga-adoptopenjdk-11.0.10+9
    */
    public static final Pattern PATTERN = Pattern.compile("^(?<" + OS + ">[^-]*)-" +
                                                        "(?<" + ARCH + ">[^-]*)-" +
                                                        "(?<" + JVM_IMPL + ">[^-]*)-" +
                                                        "(?<" + IMAGE_TYPE + ">[^-]*)-" +
                                                        "(?<" + HEAP_SIZE + ">[^-]*)-" +
                                                        "(?<" + PROJECT + ">[^-]*)-" +
                                                        "(?<" + RELEASE_TYPE + ">[^-]*)-" +
                                                        "(?<" + VENDOR + ">[^-]*)-" +
                                                        "(?<" + VERSION + ">[^-]*)$", Pattern.CASE_INSENSITIVE);

    //TODO: Problem with error handling (https://github.com/quarkusio/quarkus/issues/7883)
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

    @Inject
    Template download;

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("thank-you/{file}")
    public TemplateInstance get(@PathParam("file") String file) throws IOException, InterruptedException {
        Matcher matcher = PATTERN.matcher(file);
        if (!matcher.find()) {
            //TODO: In the future better exception message -> Something like: Version pattern is not valid.
            throw new NotFoundException("version not found!");
        }
        String os = matcher.group(OS),
                arch = matcher.group(ARCH),
                jvmImpl = matcher.group(JVM_IMPL),
                imageType = matcher.group(IMAGE_TYPE),
                heapSize = matcher.group(HEAP_SIZE),
                project = matcher.group(PROJECT),
                releaseType = matcher.group(RELEASE_TYPE),
                vendor = matcher.group(VENDOR),
                version = matcher.group(VERSION);
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