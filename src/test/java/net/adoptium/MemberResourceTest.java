package net.adoptium;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Assert;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MemberResourceTest extends MemberResource{
    // Path of the testing json files
    private final String VALIDJSON =  "/json/members_valid.json";
    private final String INVALIDFIELDJSON = "/json/members_invalidField.json";
    private final String INVALIDFORMATJSON = "/json/members_invalidFormat.json";
    private final String INVALIDPATHJSON = "/josn/members_valid.json";
    private final String INVALIDLINKJSON = "/json/members_invalidLink.json";
    private final String INVALID_LOGO_JSON = "/json/members_invalidLogo.json";
    private final String INVALID_ORGTYPE_JSON = "/json/members_invalidOrgType.json";

    @Test
    void loadValidJSONURL() {
        try {
            assertNotNull(this.loadJSONURL(VALIDJSON));
        }catch(FileNotFoundException e){
            fail("Couldn't load valid JSON URL!");
        }
    }

    @Test
    void loadInvalidJSONURL() {
        assertThrows(FileNotFoundException.class, () -> this.loadJSONURL(INVALIDPATHJSON));
    }

    @Test
    void getListOfValidMembers(){
        try {
            URL url = this.loadJSONURL(VALIDJSON);
            List<Member> memberList = this.getListOfMembers(url);
            assertEquals(3, memberList.size());
        } catch(IOException e){
            fail();
        }
    }

    @Test
    void getListOfInvalidMembers(){
        try {
            URL fieldurl = this.loadJSONURL(INVALIDFIELDJSON);
            URL formaturl = this.loadJSONURL(INVALIDFORMATJSON);
            assertThrows(IOException.class, () -> this.getListOfMembers(fieldurl));
            assertThrows(IOException.class, () -> this.getListOfMembers(formaturl));
        }catch(FileNotFoundException e){
            fail();
        }
    }

    @Test
    void sortMemberListAlphabetically(){
        try {
            URL url = this.loadJSONURL(VALIDJSON);
            List<Member> memberList = getListOfMembers(url);
            sortMemberListAlphabetically(memberList);
            assertEquals("IBM", memberList.get(0).getMemberName());
            assertEquals("Microsoft", memberList.get(1).getMemberName());
            assertEquals("New Relic", memberList.get(2).getMemberName());
        } catch(IOException | NullPointerException e){
            fail();
        }
    }

    @Test
    void sortEmptyMemberListAlphabetically(){
        List<Member> emptyList = null;
        assertThrows(NullPointerException.class, () -> sortMemberListAlphabetically(emptyList));
    }

    @Test
    void checkValidationOfMemberLink(){
        try {
            URL url = this.loadJSONURL(VALIDJSON);
            List<Member> memberList = getListOfMembers(url);

            for(Member member : memberList){
                checkValidationOfMemberLink(member);
                assertTrue(member.getIsURLValid());
            }
        }catch(IOException e){
            fail();
        }
    }

    @Test
    void checkValidationOfInvalidMemberLink(){
        try {
            URL url = this.loadJSONURL(INVALIDLINKJSON);
            List<Member> memberList = getListOfMembers(url);

            for(Member member : memberList){
                checkValidationOfMemberLink(member);
                assertFalse(member.getIsURLValid());
            }
        }catch(IOException e){
            fail();
        }
    }

    @Test
    void checkValidationOfMemberLogo(){
        try {
            URL url = this.loadJSONURL(VALIDJSON);
            List<Member> memberList = getListOfMembers(url);

            for(Member member : memberList){
                checkValidationOfMemberLogo(member);
                assertTrue(member.getIsImageFormatValid());
            }
        }catch(IOException e){
            fail();
        }
    }

    @Test
    void checkValidationOfInvalidMemberLogo(){
        try {
            URL url = this.loadJSONURL(INVALID_LOGO_JSON);
            List<Member> memberList = getListOfMembers(url);

            for(Member member : memberList){
                checkValidationOfMemberLogo(member);
                assertFalse(member.getIsImageFormatValid());
            }
        }catch(IOException e){
            fail();
        }
    }

    @Test
    void addMemberToCorrespondingMemberList(){
        try {
            URL url = this.loadJSONURL(VALIDJSON);
            List<Member> memberList = getListOfMembers(url);

            for(Member member : memberList){
                assertDoesNotThrow(() -> addMemberToCorrespondingMemberList(member));
            }
        }catch(IOException e){
            fail();
        }
    }

    @Test
    void addMemberWithInvalidOrgTypeToCorrespondingMemberList(){
        try {
            URL url = this.loadJSONURL(INVALID_ORGTYPE_JSON);
            List<Member> memberList = getListOfMembers(url);

            for(Member member : memberList){
                assertThrows(IllegalArgumentException.class, () -> addMemberToCorrespondingMemberList(member));
            }
        }catch(IOException e){
            fail();
        }
    }

    @Test
    void IntegrationTest(){
        String memberName1 = "Microsoft";
        String memberLink1 = "https://microsoft.com/";
        String memberLogo1 = "assets/memberLogos/microsoft.svg";
        String memberAltText1 = "Microsoft Logo";
        String organizationType1 = "strategic";

        String memberName2 = "New Relic";
        String memberLink2 = "https://newrelic.com/";
        String memberLogo2 = "assets/memberLogos/newrelic.svg";
        String memberAltText2 = "New Relic Logo";
        String organizationType2 = "participant";

        String memberName3 = "IBM";
        String memberLink3 = "https://ibm.com/";
        String memberLogo3 = "assets/memberLogos/ibm.svg";
        String memberAltText3 = "IBM Logo";
        String organizationType3 = "enterprise";

        MemberResource memberResource = new MemberResource(VALIDJSON);

        // Get the first Member
        Member strategicMember = memberResource.getStrategicMembers().get(0);
        Member participantMember = memberResource.getParticipantMembers().get(0);
        Member enterpriseMember = memberResource.getEnterpriseMembers().get(0);

        assertAll(
                () -> assertEquals(memberName1, strategicMember.getMemberName()),
                () -> assertEquals(memberLink1, strategicMember.getMemberLink()),
                () -> assertEquals(memberLogo1, strategicMember.getMemberLogo()),
                () -> assertEquals(memberAltText1, strategicMember.getMemberAltText()),
                () -> assertEquals(organizationType1, strategicMember.getOrganizationType())
        );

        assertAll(
                () -> assertEquals(memberName2, participantMember.getMemberName()),
                () -> assertEquals(memberLink2, participantMember.getMemberLink()),
                () -> assertEquals(memberLogo2, participantMember.getMemberLogo()),
                () -> assertEquals(memberAltText2, participantMember.getMemberAltText()),
                () -> assertEquals(organizationType2, participantMember.getOrganizationType())
        );

        assertAll(
                () -> assertEquals(memberName3, enterpriseMember.getMemberName()),
                () -> assertEquals(memberLink3, enterpriseMember.getMemberLink()),
                () -> assertEquals(memberLogo3, enterpriseMember.getMemberLogo()),
                () -> assertEquals(memberAltText3, enterpriseMember.getMemberAltText()),
                () -> assertEquals(organizationType3, enterpriseMember.getOrganizationType())
        );
    }
}
