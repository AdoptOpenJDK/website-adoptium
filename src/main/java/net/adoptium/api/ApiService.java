package net.adoptium.api;

import net.adoptium.api.model.AvailableReleasesResponse;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.Dependent;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/v3")
@RegisterRestClient
@Dependent
public interface ApiService {
    @GET
    @Path("/info/available_releases")
    @Produces(MediaType.APPLICATION_JSON)
    AvailableReleasesResponse getAvailableReleases();

    /*@GET
    @Path("/assets/feature_releases/{version}/{rel}?os={os}&architecture={arch}&jvm_impl={jvmImpl}")
    @Produces(MediaType.APPLICATION_JSON)
    String getFeatureReleases(int version, String rel, String os, String arch, String jvmImpl);*/
}
