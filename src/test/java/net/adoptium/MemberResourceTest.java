package net.adoptium;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MemberResourceTest{
    private MemberResource memberResource;
    private URL validURL, invalidFieldURL, invalidFormatURL, invalidLinkURL, invalidLogoURL, invalidOrgTypeURL;
    List<Member> validMemberList;
    // Path of the testing json files
    private final String VALID_JSON =  "/json/members_valid.json";
    private final String INVALID_FIELD_JSON = "/json/members_invalidField.json";
    private final String INVALID_FORMAT_JSON = "/json/members_invalidFormat.json";
    private final String INVALID_PATH_JSON = "/josn/members_valid.json";
    private final String INVALID_LINK_JSON = "/json/members_invalidLink.json";
    private final String INVALID_LOGO_JSON = "/json/members_invalidLogo.json";
    private final String INVALID_ORGTYPE_JSON = "/json/members_invalidOrgType.json";

    @BeforeAll
    void initialize(){
        memberResource = new MemberResource(VALID_JSON);
        try {
            validURL = memberResource.loadJSONURL(VALID_JSON);
            invalidFieldURL = memberResource.loadJSONURL(INVALID_FIELD_JSON);
            invalidFormatURL = memberResource.loadJSONURL(INVALID_FORMAT_JSON);
            invalidLinkURL = memberResource.loadJSONURL(INVALID_LINK_JSON);
            invalidLogoURL = memberResource.loadJSONURL(INVALID_LOGO_JSON);
            invalidOrgTypeURL = memberResource.loadJSONURL(INVALID_ORGTYPE_JSON);
            //validMemberList = memberResource.getListOfMembers(validURL);
        }catch(IOException e){
            fail();
        }
    }

    @Test
    void loadValidJSONURL() {
        assertAll(
                () -> assertDoesNotThrow(() -> memberResource.loadJSONURL(VALID_JSON)),
                () -> assertNotNull(memberResource.loadJSONURL(VALID_JSON))
        );
    }

    @Test
    void loadInvalidJSONURL() {
        assertThrows(FileNotFoundException.class, () -> memberResource.loadJSONURL(INVALID_PATH_JSON));
    }

    @Test
    void getListOfValidMembers(){
        try {
            List<Member> memberList = memberResource.getListOfMembers(validURL);
            assertEquals(3, memberList.size());
        } catch(IOException e){
            fail();
        }
    }

    @Test
    void getListOfInvalidMembers(){
        assertThrows(IOException.class, () -> memberResource.getListOfMembers(invalidFieldURL));
        assertThrows(IOException.class, () -> memberResource.getListOfMembers(invalidFormatURL));
    }

    @Test
    void sortMemberListAlphabetically(){
        try {
            List<Member> memberList = memberResource.getListOfMembers(validURL);
            memberResource.sortMemberListAlphabetically(memberList);
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
        assertThrows(NullPointerException.class, () -> memberResource.sortMemberListAlphabetically(emptyList));
    }

    @Test
    void checkValidationOfMemberLink(){
        try {
            List<Member> memberList = memberResource.getListOfMembers(validURL);

            for(Member member : memberList){
                memberResource.checkValidationOfMemberLink(member);
                assertTrue(member.getIsURLValid());
            }
        }catch(IOException e){
            fail();
        }
    }

    @Test
    void checkValidationOfInvalidMemberLink(){
        try {
            List<Member> memberList = memberResource.getListOfMembers(invalidLinkURL);

            for(Member member : memberList){
                memberResource.checkValidationOfMemberLink(member);
                assertFalse(member.getIsURLValid());
            }
        }catch(IOException e){
            fail();
        }
    }

    @Test
    void checkValidationOfMemberLogo(){
        try {
            List<Member> memberList = memberResource.getListOfMembers(validURL);

            for(Member member : memberList){
                memberResource.checkValidationOfMemberLogo(member);
                assertTrue(member.getIsImageFormatValid());
            }
        }catch(IOException e){
            fail();
        }
    }

    @Test
    void checkValidationOfInvalidMemberLogo(){
        try {
            List<Member> memberList = memberResource.getListOfMembers(invalidLogoURL);

            for(Member member : memberList){
                memberResource.checkValidationOfMemberLogo(member);
                assertFalse(member.getIsImageFormatValid());
            }
        }catch(IOException e){
            fail();
        }
    }

    @Test
    void addMemberToCorrespondingMemberList(){
        try {
            List<Member> memberList = memberResource.getListOfMembers(validURL);

            for(Member member : memberList){
                assertDoesNotThrow(() -> memberResource.addMemberToCorrespondingMemberList(member));
            }
        }catch(IOException e){
            fail();
        }
    }

    @Test
    void addMemberWithInvalidOrgTypeToCorrespondingMemberList(){
        try {
            List<Member> memberList = memberResource.getListOfMembers(invalidOrgTypeURL);

            for(Member member : memberList){
                assertThrows(IllegalArgumentException.class, () -> memberResource.addMemberToCorrespondingMemberList(member));
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

        memberResource = new MemberResource(VALID_JSON);

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