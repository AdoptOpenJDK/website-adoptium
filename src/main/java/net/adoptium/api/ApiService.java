package net.adoptium.api;

import net.adoptopenjdk.api.v3.models.BinaryAssetView;
import net.adoptopenjdk.api.v3.models.JvmImpl;
import net.adoptopenjdk.api.v3.models.Release;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.Dependent;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/v3")
@RegisterRestClient
@Dependent
public interface ApiService {

    @GET
    @Path("/assets/latest/{feature_version}/{jvm_impl}")
    @Produces(MediaType.APPLICATION_JSON)
    List<BinaryAssetView> getAvailableReleases(@PathParam("feature_version") int featureVersion,
                                               @PathParam("jvm_impl") JvmImpl jvmImpl);

    @GET
    @Path("/assets/version/{version}") //?architecture={arch}&heap_size={heapSize}&image_type={imageType}&jvm_impl={jvmImpl}&os={os}&project={project}&release_type={releaseType}&vendor={vendor}
    @Produces(MediaType.APPLICATION_JSON)
    List<Release> getRelease(@PathParam("version") String version,
                             @QueryParam("architecture") String arch,
                             @QueryParam("heap_size") String heapSize,
                             @QueryParam("image_type") String imageType,
                             @QueryParam("jvm_impl") String jvmImpl,
                             @QueryParam("os") String os,
                             @QueryParam("project") String project,
                             @QueryParam("release_type") String releaseType,
                             @QueryParam("vendor") String vendor);
}