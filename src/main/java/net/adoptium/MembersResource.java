package net.adoptium;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
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
public class MembersResource {
    private static final Logger LOG = Logger.getLogger(MembersResource.class);
    private static final String EMPTY_STRING = "";
    private static final String JSON_PATH = "json/members.json";

    @Inject
    Template members;

    public MembersResource() {
        InputStream is = loadJSONInputStream(JSON_PATH);
        String json = parseInputStreamToString(is);

        if(json.equals(EMPTY_STRING)){
            throw new IllegalArgumentException("file not found!");
        }else{
            List<Member> memberList = getMemberList(json);

            for(Member m : memberList){
                // TODO: insert Members into HTML page, for loop probably not needed
            }
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
        return members.instance();
    }
}