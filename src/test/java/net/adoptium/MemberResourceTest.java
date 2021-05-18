package net.adoptium;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import org.junit.Assert;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MemberResourceTest extends MemberResource{
    private MemberResource memberResource;
    private URL validJsonURL, invalidJsonURL;
    // Path of the testing json files
    private final String VALIDJSON =  "/json/members_valid.json";
    private final String INVALIDFIELDJSON = "/json/members_invalidField.json";
    private final String INVALIDFORMATJSON = "/json/members_invalidFormat.json";
    private final String INVALIDPATHJSON = "/josn/members_valid.json";
    private final String INVALIDLINKJSON = "/json/members_invalidLink.json";

    @BeforeAll
    public void init(){
        //memberResource = new MemberResource();

    }

    @Test
    void loadValidJSONURL() {
        try {
            Assert.assertNotNull(this.loadJSONURL(VALIDJSON));
        }catch(FileNotFoundException e){
            Assert.fail("Couldn't load valid JSON URL!");
        }
    }

    @Test
    void loadInvalidJSONURL() {
        Assert.assertThrows(FileNotFoundException.class, () -> this.loadJSONURL(INVALIDPATHJSON));
    }

    @Test
    void getListOfValidMembers(){
        try {
            URL url = this.loadJSONURL(VALIDJSON);
            List<Member> memberList = this.getListOfMembers(url);
            Assert.assertEquals(3, memberList.size());
        } catch(IOException e){
            Assert.fail();
        }
    }

    @Test
    void getListOfInvalidMembers(){
        try {
            URL fieldurl = this.loadJSONURL(INVALIDFIELDJSON);
            URL formaturl = this.loadJSONURL(INVALIDFORMATJSON);
            Assert.assertThrows(IOException.class, () -> this.getListOfMembers(fieldurl));
            Assert.assertThrows(IOException.class, () -> this.getListOfMembers(formaturl));
        }catch(FileNotFoundException e){
            Assert.fail();
        }
    }

    @Test
    void sortMemberListAlphabetically(){
        try {
            URL url = this.loadJSONURL(VALIDJSON);
            List<Member> memberList = getListOfMembers(url);
            sortMemberListAlphabetically(memberList);
            Assert.assertEquals("IBM", memberList.get(0).getMemberName());
            Assert.assertEquals("Microsoft", memberList.get(1).getMemberName());
            Assert.assertEquals("New Relic", memberList.get(2).getMemberName());
        } catch(IOException | NullPointerException e){
            Assert.fail();
        }
    }

    @Test
    void sortEmptyMemberListAlphabetically(){
        List<Member> emptyList = null;
        Assert.assertThrows(NullPointerException.class, () -> sortMemberListAlphabetically(emptyList));
    }

    @Test
    void checkValidationOfMemberLink(){
        try {
            URL url = this.loadJSONURL(VALIDJSON);
            List<Member> memberList = getListOfMembers(url);
            sortMemberListAlphabetically(memberList);

            for(Member member : memberList){
                checkValidationOfMemberLink(member);
                Assert.assertTrue(member.getIsURLValid());
            }
        }catch(IOException e){
            Assert.fail();
        }
    }

    @Test
    void checkValidationOfInvalidMemberLink(){
        try {
            URL url = this.loadJSONURL(INVALIDLINKJSON);
            List<Member> memberList = getListOfMembers(url);
            sortMemberListAlphabetically(memberList);

            for(Member member : memberList){
                checkValidationOfMemberLink(member);
                Assert.assertFalse(member.getIsURLValid());
            }
        }catch(IOException e){
            Assert.fail();
        }
    }

    @DisplayName("Test parsing a JSON file")
    @Test
    void parseJSONTest() throws IOException {
        /**
         * This test method will only test the deserialization of
         * the first entry of the members_valid.json file.
         *
         * To test a new entry, simply inject a new entry at
         * the beginning of the members_valid.json file and make sure
         * that the injected info matches with the five Strings below.
         * These five Strings must be initialized according to the new
         * injected entry you want to test.
         */
        String memberName = "Microsoft";
        String memberLink = "https://microsoft.com/";
        String memberLogo = "assets/memberLogos/microsoft.svg";
        String memberAltText = "Microsoft Logo";
        String organizationType = "strategic";

        validJsonURL = memberResource.loadJSONURL(VALIDJSON);

        // Deserialize JSON file by URL
        List<Member> memberList = memberResource.getListOfMembers(validJsonURL);
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
