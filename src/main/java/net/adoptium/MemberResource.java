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
import java.io.FileNotFoundException;
import java.io.IOException;
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

    private boolean canLoadJSON = true;

    @Inject
    Template members;

    public MemberResource() {
        setUpMembers(JSON_PATH);
    }

    public MemberResource(String jsonPath) {
        setUpMembers(jsonPath);
    }

    protected void setUpMembers(String jsonPath) {
        URL jsonUrl = null;
        try {
            jsonUrl = loadJSONURL(jsonPath);

            List<Member> memberList = getListOfMembers(jsonUrl);

            sortMemberListAlphabetically(memberList);

            for (Member member : memberList) {
                checkValidationOfMemberLink(member);
                checkValidationOfMemberLogo(member);
                filterMembersByOrganisationType(member);
            }
        } catch (FileNotFoundException e) {
            canLoadJSON = false;
            LOG.errorf("Invalid JSON Path, couldn't find resource. JSON Path: %s", JSON_PATH);
        } catch (IOException e) {
            canLoadJSON = false;
            LOG.errorf("Could not deserialize JSON URL to Member Objects. Error: %s", e.getMessage());
        } catch (NullPointerException e) {
            canLoadJSON = false;
            LOG.errorf("Error: MemberList is empty!");
        }
    }

    protected void sortMemberListAlphabetically(List<Member> memberList) throws NullPointerException {
        Collections.sort(memberList);
    }

    protected void checkValidationOfMemberLink(Member member) {
        member.validateURL();
        if (!member.getIsURLValid()) {
            LOG.warnf("Invalid URL of Member: %s", member.getMemberName());
        }
    }

    protected void checkValidationOfMemberLogo(Member member) {
        member.validateImageFormat();
        if (!member.getIsImageFormatValid()) {
            LOG.warnf("Invalid Logo Format/Path of Member: %s. Format: ([a-zA-Z0-9-_]+/)*[a-zA-Z0-9-_]+\\.(svg)",
                    member.getMemberName());
        }
    }

    protected void filterMembersByOrganisationType(Member member) {
        try {
            addMemberToCorrespondingMemberList(member);
        } catch (IllegalArgumentException e) {
            LOG.warnf("While filtering members, missing Organization type." +
                    "\nMember Name: %s, Member OrgType: %s", member.getMemberName(), member.getOrganizationType());
        }
    }

    protected void addMemberToCorrespondingMemberList(Member member) throws IllegalArgumentException {
        // valueOf() throws IllegalArgumentException whenever Organization Type doesnt match with any Enum.
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
        }
    }

    protected List<Member> getListOfMembers(URL url) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Member> listOfMembers;
        // objectMapper.readValue() throws IOException when JSON couldn't be deserialized
        listOfMembers = objectMapper.readValue(url, new TypeReference<List<Member>>() {
        });
        return listOfMembers;
    }

    protected URL loadJSONURL(String is) throws FileNotFoundException {
        URL jsonURL = getClass().getClassLoader().getResource(is);
        if (jsonURL == null) {
            throw new FileNotFoundException("Invalid JSON path.");
        }
        return jsonURL;
    }

    public List<Member> getStrategicMembers() {
        return strategicMembers;
    }

    public List<Member> getEnterpriseMembers() {
        return enterpriseMembers;
    }

    public List<Member> getParticipantMembers() {
        return participantMembers;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() {
        return members.data("strategicMembers", strategicMembers)
                .data("enterpriseMembers", enterpriseMembers)
                .data("participantMembers", participantMembers)
                .data("canLoadJSON", canLoadJSON);
    }
}