package net.adoptium;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.vertx.ext.web.RoutingContext;
import net.adoptium.config.ApplicationConfig;
import net.adoptium.model.DocumentationTemplate;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/documentation")
public class DocumentationResource {
    private static final Logger LOG = Logger.getLogger(DocumentationResource.class);


    @Inject
    ApplicationConfig appConfig;

    @Inject
    RoutingContext routingContext;

    /**
     * Checked Templates ensure type-safety in html templating.
     */
    @CheckedTemplate
    public static class Templates {
        /**
         * The method name of a `static native TemplateInstance` refers to the name of a .html file in templates/DownloadResource.
         *
         * @param template all data accessible by the template
         * @return a Template with values from template filled in
         */
        public static native TemplateInstance documentation(DocumentationTemplate template);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/{doc-name}")
    public TemplateInstance get(@PathParam("doc-name") String docName, @HeaderParam("Accept-Language") String acceptLanguage) {
        DocumentationTemplate template = new DocumentationTemplate(docName, acceptLanguage, appConfig.getDefaultLocale().getLanguage());
        return DocumentationResource.Templates.documentation(template).data("header", routingContext.get("header"));
    }
}
