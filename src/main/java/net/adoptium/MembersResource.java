package net.adoptium;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/members")
public class MembersResource {
    private static final Logger LOG = Logger.getLogger(MembersResource.class);

    @Inject
    Template members;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() throws Exception {
        return members.instance();
    }
}