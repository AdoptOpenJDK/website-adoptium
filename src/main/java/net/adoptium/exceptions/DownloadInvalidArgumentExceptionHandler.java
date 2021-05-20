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
public class DownloadInvalidArgumentExceptionHandler implements ExceptionMapper<DownloadInvalidArgumentException> {

    @Inject
    Engine engine;

    @Inject
    RoutingContext rc;

    @Override
    @Produces(MediaType.TEXT_HTML)
    public Response toResponse(DownloadInvalidArgumentException exception) {
        HeaderTemplate header = rc.get("header");

        String template = DownloadResource.Templates.error(
                new DownloadErrorTemplate(
                        getI18N(header.getLocale(), "exceptionVersionNotFound"), getI18N(header.getLocale(), "exceptionGenericHint")
                ))
                .data("header", header)
                .setAttribute("locale", header.getLocale())
                .render();
        return Response.status(Response.Status.NOT_FOUND).entity(template).type(MediaType.TEXT_HTML).build();
    }

    /**
     * TODO: as soon as <a href="https://github.com/quarkusio/quarkus/issues/12792">quarkus issue #12792</a> is resolved,
     *       all ExceptionHandlers can be cleaned up
     *       as of 18.05.2020, it's merged in main
     * Qute does not support converting a string id inside a template variable to it's translation.
     * {msg:exceptionGenericHint} works, {msg:{variable-name}} (and similar) cannot be resolved.
     *
     * @param locale Translation language for the string id
     * @param id     AppMessages String id to translate
     * @return translated
     */
    private String getI18N(String locale, String id) {
        return engine.parse("{msg:" + id + "}").instance().setAttribute("locale", locale).render();
    }
}
