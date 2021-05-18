package net.adoptium;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;
import net.adoptium.model.DocumentationTemplate;
import net.adoptium.model.ThankYouTemplate;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/documentation")
public class DocumentationResource {
    private static final Logger LOG = Logger.getLogger(DownloadResource.class);

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
    public TemplateInstance get(@PathParam("doc-name") String docName) {
        DocumentationTemplate template = getImpl(docName);
        return DocumentationResource.Templates.documentation(template);
    }

    private DocumentationTemplate getImpl(String docName) {
        return new DocumentationTemplate(docName);
    }
}
