package net.adoptium;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import net.adoptium.model.OrganizationType;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Path("/members")
public class MemberResource {
    private static final Logger LOG = Logger.getLogger(MemberResource.class);
    private static final String JSON_PATH = "json/members.json";

    private final List<Member> strategicMembers = new ArrayList<>();
    private final List<Member> enterpriseMembers = new ArrayList<>();
    private final List<Member> participantMembers = new ArrayList<>();

    @Inject
    Template members;

    public MemberResource() {
        URL json_url = loadJSONURL(JSON_PATH);
        List<Member> memberList = getMemberList(json_url);

        filterMembersByOrganisationType(memberList);
    }

    private void filterMembersByOrganisationType(List<Member> memberList){
        for(Member member : memberList){
            addMemberToCorrespondingMemberList(member);
        }
    }

    private void addMemberToCorrespondingMemberList(Member member){
        OrganizationType orgType = OrganizationType.valueOf(member.getOrganizationType().toUpperCase());
        switch (orgType) {
            case STRATEGIC:
                strategicMembers.add(member);
                break;
            case ENTERPRISE:
                enterpriseMembers.add(member);
                break;
            case PARTICIPANT:
                participantMembers.add(member);
                break;
            default:
                LOG.warnf("While filtering members, missing Organization type.");
                break;
        }
    }

    public static List<Member> getMemberList(URL url) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Member> listOfMembers = new ArrayList<>();
        try {
            listOfMembers =  objectMapper.readValue(url, new TypeReference<List<Member>>(){});
        } catch (IOException e) {
            LOG.errorf("Error: Could not deserialize JSON String to Object", url);
        }
        return listOfMembers;
    }

    protected URL loadJSONURL(String is){
        return getClass().getClassLoader().getResource(is);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() throws Exception {
        return members.data("strategicMembers", strategicMembers);
    }
}