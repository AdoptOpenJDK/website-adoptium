package net.adoptium.api;

import net.adoptopenjdk.api.v3.models.BinaryAssetView;
import net.adoptopenjdk.api.v3.models.JvmImpl;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.Dependent;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
}