package net.adoptium;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.vertx.ext.web.RoutingContext;
import net.adoptium.model.Member;
import net.adoptium.model.OrganizationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOG = LoggerFactory.getLogger(MemberResource.class);

    private static final String JSON_PATH = "json/members.json";

    private final List<Member> strategicMembers = new ArrayList<>();

    private final List<Member> enterpriseMembers = new ArrayList<>();

    private final List<Member> participantMembers = new ArrayList<>();

    private boolean canLoadJSON = true;

    @Inject
    private Template members;

    @Inject
    private RoutingContext routingContext;

    public MemberResource() {
        setUpMembers(JSON_PATH);
    }

    public MemberResource(final String jsonPath) {
        setUpMembers(jsonPath);
    }

    protected void setUpMembers(final String jsonPath) {
        URL jsonUrl = null;
        try {
            jsonUrl = loadJSONURL(jsonPath);

            final List<Member> memberList = getListOfMembers(jsonUrl);

            sortMemberListAlphabetically(memberList);

            for (final Member member : memberList) {
                checkValidationOfMemberLink(member);
                checkValidationOfMemberLogo(member);
                filterMembersByOrganisationType(member);
            }
        } catch (final FileNotFoundException e) {
            canLoadJSON = false;
            LOG.error("Invalid JSON Path, couldn't find resource. JSON Path: {}", JSON_PATH);
        } catch (final IOException e) {
            canLoadJSON = false;
            LOG.error("Could not deserialize JSON URL to Member Objects. Error: {}", e.getMessage());
        } catch (final NullPointerException e) {
            canLoadJSON = false;
            LOG.error("Error: MemberList is empty!");
        }
    }

    protected void sortMemberListAlphabetically(final List<Member> memberList) throws NullPointerException {
        Collections.sort(memberList);
    }

    protected void checkValidationOfMemberLink(final Member member) {
        member.validateURL();
        if (!member.getIsURLValid()) {
            LOG.warn("Invalid URL of Member: {}. Format: (http(s)?:)//(www.)?([a-zA-Z0-9-]{1,20}.){1,5}[a-zA-Z0-9-]{2,5}(/)?", member.getMemberName());
        }
    }

    protected void checkValidationOfMemberLogo(final Member member) {
        member.validateImageFormat();
        if (!member.getIsImageFormatValid()) {
            LOG.warn("Invalid Logo Format/Path of Member: {}. Format: ([a-zA-Z0-9-_]{1,20}/){1,10}[a-zA-Z0-9-_]+.(svg)",
                    member.getMemberName());
        }
    }

    protected void filterMembersByOrganisationType(final Member member) {
        try {
            addMemberToCorrespondingMemberList(member);
        } catch (final IllegalArgumentException e) {
            LOG.warn("While filtering members, missing Organization type. Member Name: {}, Member OrgType: {}", member.getMemberName(), member.getOrganizationType());
        }
    }

    protected void addMemberToCorrespondingMemberList(final Member member) throws IllegalArgumentException {
        // valueOf() throws IllegalArgumentException whenever Organization Type doesnt match with any Enum.
        final OrganizationType orgType = OrganizationType.valueOf(member.getOrganizationType().toUpperCase());
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

    protected List<Member> getListOfMembers(final URL url) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final List<Member> listOfMembers;
        // objectMapper.readValue() throws IOException when JSON couldn't be deserialized
        listOfMembers = objectMapper.readValue(url, new TypeReference<>() {
        });
        return listOfMembers;
    }

    protected URL loadJSONURL(final String is) throws FileNotFoundException {
        final URL jsonURL = getClass().getClassLoader().getResource(is);
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
                .data("canLoadJSON", canLoadJSON)
                .data("header", routingContext.get("header"));
    }
}