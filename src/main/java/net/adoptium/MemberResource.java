package net.adoptium;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Path("/members")
public class MemberResource {
    private static final Logger LOG = Logger.getLogger(MemberResource.class);
    private static final String EMPTY_STRING = "";
    private static final String JSON_PATH = "json/members.json";

    List<Member> strategicMembers = new ArrayList<>();
    List<Member> enterpriseMembers = new ArrayList<>();
    List<Member> participantMembers = new ArrayList<>();

    @Inject
    Template members;

    public MemberResource() {
        InputStream is = loadJSONInputStream(JSON_PATH);
        String json = parseInputStreamToString(is);

        if(json.equals(EMPTY_STRING)){
            throw new IllegalArgumentException("file not found!");
        }else{
            List<Member> memberList = getMemberList(json);

            for(Member member : memberList){
                // TODO: this way the json file is read 3 times in total, could maybe improve by filtering on the stream in parseInputStreamToString()

                addMemberToCorrespondingMemberList(member);
            }
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

    public static List<Member> getMemberList(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Member> listOfMembers = new ArrayList<>();
        try {
            listOfMembers =  objectMapper.readValue(json, new TypeReference<List<Member>>(){});
        } catch (JsonProcessingException e) {
            LOG.errorf("Error: Could not deserialize JSON String to Object", json);
        }
        return listOfMembers;
    }

    private InputStream loadJSONInputStream(String path){
        return getClass().getClassLoader().getResourceAsStream(path);
    }

    private static String parseInputStreamToString(InputStream is) {
        String parsedString = EMPTY_STRING;
        try (InputStreamReader streamReader =
                     new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {

            String line;
            StringBuilder sb = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            parsedString = sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return parsedString;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() throws Exception {
        return members.data("strategicMembers", strategicMembers);
    }
}