package net.adoptium;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import org.junit.Assert;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MemberResourceTest {
    private MemberResource memberResource;
    private URL validJsonURL, invalidJsonURL;
    // Path of the testing json files
    private final String VALIDJSON =  "json/members.json";
    private final String INVALIDJSON = "src/test/java/net/adoptium/json/members_invalid.json";

    @BeforeAll
    public void init(){
        memberResource = new MemberResource();
        validJsonURL = memberResource.loadJSONURL(VALIDJSON);
    }

    @DisplayName("Test parsing a JSON file")
    @Test
    void parseJSONTest(){
        /**
         * This test method will only test the deserialization of
         * the first entry of the members.json file.
         *
         * To test a new entry, simply inject a new entry at
         * the beginning of the members.json file and make sure
         * that the injected info matches with the five Strings below.
         * These five Strings must be initialized according to the new
         * injected entry you want to test.
         */
        String memberName = "Microsoft";
        String memberLink = "https://microsoft.com/";
        String memberLogo = "assets/memberLogos/microsoft.svg";
        String memberAltText = "Microsoft Logo";
        String organizationType = "strategic";

        // Deserialize JSON file by URL
        List<Member> memberList = memberResource.getMemberList(validJsonURL);
        // Get the first Member
        Member testMember = memberList.get(0);

        Assert.assertEquals(memberName, testMember.getMemberName());
        Assert.assertEquals(memberLink, testMember.getMemberLink());
        Assert.assertEquals(memberLogo, testMember.getMemberLogo());
        Assert.assertEquals(memberAltText, testMember.getMemberAltText());
        Assert.assertEquals(organizationType, testMember.getOrganizationType());
    }

    @DisplayName("Test reading a valid JSON file")
    @Test
    void readValidJSON(){
        Assert.assertTrue(isJSONValid(validJsonURL));
    }

    private boolean isJSONValid(URL jsonURL) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(jsonURL);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
