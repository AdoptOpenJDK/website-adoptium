package net.adoptium.exceptions;

import io.quarkus.qute.Template;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class DownloadInvalidArgumentExceptionHandler implements ExceptionMapper<DownloadInvalidArgumentException> {

    @Inject
    Template downloadError;

    @Override
    @Produces(MediaType.TEXT_HTML)
    public Response toResponse(DownloadInvalidArgumentException exception) {
        String template = downloadError
                .data("msg", exception.getMessage())
                .data("suggestion", exception.getSuggestion())
                .render();
        return Response.status(Response.Status.NOT_FOUND).entity(template).type(MediaType.TEXT_HTML).build();
    }

}