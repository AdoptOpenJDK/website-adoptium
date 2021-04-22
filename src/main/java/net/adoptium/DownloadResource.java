package net.adoptium;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import net.adoptium.api.ApiService;
import net.adoptopenjdk.api.v3.models.Package;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.adoptopenjdk.api.v3.models.*;

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
    public static final String ARGS_REGEX = "^(?<" + OS + ">[^-\\/]*)-" +
            "(?<" + ARCH + ">[^-\\/]*)-" +
            "(?<" + JVM_IMPL + ">[^-\\/]*)-" +
            "(?<" + IMAGE_TYPE + ">[^-\\/]*)-" +
            "(?<" + HEAP_SIZE + ">[^-\\/]*)-" +
            "(?<" + PROJECT + ">[^-\\/]*)-" +
            "(?<" + RELEASE_TYPE + ">[^-\\/]*)-" +
            "(?<" + VENDOR + ">[^-\\/]*)-" +
            "(?<" + VERSION + ">[^\\/]*)$";

    private static final Logger LOG = Logger.getLogger(DownloadResource.class);

    //TODO: https://github.com/quarkusio/quarkus/issues/7883
    public class NotFoundException extends RuntimeException {
        public final String msg;
        public NotFoundException(String msg) {
            this.msg = msg;
        }
    }

    /*@ServerExceptionMapper
    public Response mapException(NotFoundException x) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity("NOT FOUND!: " + x.msg)
                .build();
    }*/

    /*@Provider
    public class NotFoundExceptionHandler implements ExceptionMapper<Exception> {

        NotFoundExceptionHandler() {

        }

        @Override
        public Response toResponse(Exception exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity("exception.getMessage()").build();
        }

    }*/

    /*public static class NotFoundException extends Exception implements Serializable {

        public NotFoundException() {
            super();
        }

        public NotFoundException(String msg)   {
            super(msg);
        }

        public NotFoundException(String msg, Exception e)  {
            super(msg, e);
        }
    }

    @Provider
    public class NotFoundExceptionHandler implements ExceptionMapper<NotFoundException> {

        @Override
        public Response toResponse(NotFoundException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }

    }*/

    @Inject
    Template download;

    @Inject
    @RestClient
    ApiService api;

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("thank-you/{args}")
    public TemplateInstance get(@PathParam("args") String args) throws Exception {
        //TODO: Ask if we first should check if file matches an existing version in the api. or if we directly should send the request to the api and let the api check the version
        Pattern pattern = Pattern.compile(ARGS_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(args);
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
        String downloadLink;
        String checksum;
        List<Release> releaseList = api.getRelease(version, arch, heapSize, imageType, jvmImpl, os, project, releaseType, vendor);
        List<Binary> binaryList = Arrays.asList(releaseList.get(0).getBinaries());
        if (binaryList.size() != 1) {
            LOG.error("There are " + binaryList.size() + " binaries available! Expected just 1");
            throw new Exception("Unexpected number of binaries!");
        }
        Binary binary = binaryList.get(0);
        if (binary.getInstaller() != null) {
            Installer installer = binary.getInstaller();
            downloadLink = installer.getLink();
            checksum = installer.getChecksum();
        } else {
            Package pkg = binary.getPackage();
            downloadLink = pkg.getLink();
            checksum = pkg.getChecksum();
        }
        return download
                .data("downloadLink", downloadLink)
                .data("imageType", imageType)
                .data("version", version)
                .data("os", os)
                .data("arch", arch)
                .data("vendor", vendor)
                .data("checksum", checksum);
    }

}