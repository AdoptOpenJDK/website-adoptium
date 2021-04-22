package net.adoptium.exceptions;

import io.quarkus.qute.Template;
import io.quarkus.qute.api.ResourcePath;

import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class DownloadBinaryNotFoundExceptionHandler implements ExceptionMapper<DownloadBinaryNotFoundException> {

    @Inject
    Template downloadError;

    @Override
    @Produces(MediaType.TEXT_HTML)
    public Response toResponse(DownloadBinaryNotFoundException exception) {
        String template = downloadError
                .data("msg", exception.getMessage())
                .data("suggestion", exception.getSuggestion())
                .render();
        return Response.status(Response.Status.NOT_FOUND).entity(template).type(MediaType.TEXT_HTML).build();
    }

}