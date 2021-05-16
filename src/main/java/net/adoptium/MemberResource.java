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
import java.util.Collections;
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
        URL json_url = null;
        try {
            json_url = loadJSONURL(JSON_PATH);

            List<Member> memberList = getListOfMembers(json_url);

            sortMemberListAlphabetically(memberList);

            filterMembersByOrganisationType(memberList);
        } catch (FileNotFoundException e) {
            LOG.errorf("Invalid JSON Path, couldn't find resource. JSON Path: %s", JSON_PATH);
            // TODO: If JSON Path invalid, loads members page only with headers. Maybe show error text?
        } catch (IOException e) {
            LOG.errorf("Error: Could not deserialize JSON URL to Member Objects. e: %s", e.getMessage());
        }


    }

    private void sortMemberListAlphabetically(List<Member> memberList) {
        Collections.sort(memberList);
    }

    private void filterMembersByOrganisationType(List<Member> memberList){
        for(Member member : memberList){
            try {
                addMemberToCorrespondingMemberList(member);
            } catch (IllegalArgumentException e) {
                LOG.warnf("While filtering members, missing Organization type." +
                        "\nMember Name: %s, Member OrgType: %s", member.getMemberName(), member.getOrganizationType());
            }
        }
    }

    private void addMemberToCorrespondingMemberList(Member member) throws IllegalArgumentException {
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
        }
    }

    public static List<Member> getListOfMembers(URL url) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Member> listOfMembers = new ArrayList<>();
        // objectMapper.readValue() throws IOException when JSON couldn't be deserialized
        listOfMembers =  objectMapper.readValue(url, new TypeReference<List<Member>>(){});
        return listOfMembers;
    }

    protected URL loadJSONURL(String is) throws FileNotFoundException {
        URL jsonURL = getClass().getClassLoader().getResource(is);
        if(jsonURL == null){
            throw new FileNotFoundException("Invalid JSON path.");
        }
        return jsonURL;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() throws Exception {
        return members.data("strategicMembers", strategicMembers)
                .data("enterpriseMembers", enterpriseMembers)
                .data("participantMembers", participantMembers);
    }
}