package net.adoptium.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import net.adoptopenjdk.api.v3.models.BinaryAssetView;
import net.adoptopenjdk.api.v3.models.ImageType;
import net.adoptopenjdk.api.v3.models.JvmImpl;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.Dependent;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/v3")
@RegisterRestClient
@Dependent
public interface ApiService {

    @GET
    @Path("/assets/latest/{feature_version}/{jvm_impl}")
    @Produces(MediaType.APPLICATION_JSON)
    //@JsonInclude(JsonInclude.Include.NON_NULL)
    List<BinaryAssetView> getAvailableReleases(@PathParam("feature_version") int featureVersion,
                                               @PathParam("jvm_impl") JvmImpl jvmImpl);


    /*@GET
    @Path("/assets/feature_releases/{version}/{rel}?os={os}&architecture={arch}&jvm_impl={jvmImpl}")
    @Produces(MediaType.APPLICATION_JSON)
    String getFeatureReleases(int version, String rel, String os, String arch, String jvmImpl);*/
}