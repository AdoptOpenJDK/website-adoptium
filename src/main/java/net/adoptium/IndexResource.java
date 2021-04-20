package net.adoptium;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

import org.asciidoctor.Asciidoctor;

import java.io.File;
import java.util.HashMap;

import static org.asciidoctor.Asciidoctor.Factory.create;

@Path("/")
public class IndexResource {

    @Inject
    Template index;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() {
        Asciidoctor asciidoctor = create();
        String output = asciidoctor.convertFile(new File("/documentation/test.adoc"), new HashMap<String, Object>());
        return index.data("asciidoc", output);
    }

}

