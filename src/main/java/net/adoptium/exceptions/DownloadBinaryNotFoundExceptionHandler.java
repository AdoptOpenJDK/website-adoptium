package net.adoptium.exceptions;

import io.quarkus.qute.Engine;
import io.vertx.ext.web.RoutingContext;
import net.adoptium.DownloadResource;
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
    private Engine engine;

    @Inject
    private RoutingContext rc;

    @Override
    @Produces(MediaType.TEXT_HTML)
    public Response toResponse(final DownloadBinaryNotFoundException exception) {
        final HeaderTemplate header = rc.get("header");

        final String template = DownloadResource.Templates.error(
                new DownloadErrorTemplate(
                        getI18N(header.getLocale(), "exceptionDownloadNotFound"), getI18N(header.getLocale(), "exceptionGenericHint")
                ))
                .setAttribute("locale", header.getLocale()).data("header", header).render();
        return Response.status(Response.Status.NOT_FOUND).entity(template).type(MediaType.TEXT_HTML).build();
    }

    /**
     * TODO: as soon as <a href="https://github.com/quarkusio/quarkus/issues/12792">quarkus issue #12792</a> is resolved,
     * all ExceptionHandlers can be cleaned up
     * as of 18.05.2020, it's merged in main
     * Qute does not support converting a string id inside a template variable to it's translation.
     * {msg:exceptionGenericHint} works, {msg:{variable-name}} (and similar) cannot be resolved.
     */
    private String getI18N(final String locale, final String id) {
        return engine.parse("{msg:" + id + "}").instance().setAttribute("locale", locale).render();
    }
}
