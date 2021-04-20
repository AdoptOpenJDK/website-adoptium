package net.adoptium;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.OptionsBuilder;
import static org.asciidoctor.OptionsBuilder.options;

import java.io.File;

@Path("/")
public class IndexResource {

    @Inject
    Template index;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() {
        String output = convertHtml();
        return index.data("jam", output);
    }

    public String convertHtml() {
        Asciidoctor asciidoctor = Asciidoctor.Factory.create();
        OptionsBuilder options = options();
        options.toFile(false);
        String results = asciidoctor.convertFile(new File("classes/META-INF/resources/docs/index.html"), options);
        return results;
    }
}

