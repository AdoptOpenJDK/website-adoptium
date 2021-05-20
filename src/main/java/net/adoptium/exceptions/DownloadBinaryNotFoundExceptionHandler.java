package net.adoptium.exceptions;

import io.quarkus.qute.Engine;
import net.adoptium.DownloadResource;
import net.adoptium.config.ApplicationConfig;
import net.adoptium.model.DownloadErrorTemplate;
import net.adoptium.model.HeaderTemplate;

import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class DownloadBinaryNotFoundExceptionHandler implements ExceptionMapper<DownloadBinaryNotFoundException> {

    @Inject
    ApplicationConfig appConfig;

    @Inject
    Engine engine;

    @Override
    @Produces(MediaType.TEXT_HTML)
    public Response toResponse(DownloadBinaryNotFoundException exception) {
        String template = DownloadResource.Templates.error(
                new DownloadErrorTemplate(engine, "exceptionDownloadNotFound", "exceptionGenericHint")
        ).data("header", new HeaderTemplate(appConfig.getLocales(), appConfig.getDefaultLocale().toLanguageTag())).render();
        return Response.status(Response.Status.NOT_FOUND).entity(template).type(MediaType.TEXT_HTML).build();
    }
}