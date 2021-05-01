package net.adoptium;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import org.junit.Assert;
import java.util.List;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MembersResourceTest {


    @DisplayName("Test loading a JSON file")
    @Test
    void parseJSONTest(){
        String json = "{\"memberName\":\"Microsoft\",\"memberLink\":\"https://microsoft.com/\", \"memberLogo\":\"assets/memberLogos/microsoft.svg\", \"memberAltText\":\"Microsoft Logo\", \"organizationType\":\"\"}";
        try {
            MembersResource memberResource = new MembersResource();
            List<Member> memberList = memberResource.getMemberList(json);
            Member testMember = memberList.get(memberList.size() - 1);
            Assert.assertEquals("Microsoft", testMember.getMemberName());
            Assert.assertEquals("https://microsoft.com/", testMember.getMemberLink());
            Assert.assertEquals("assets/memberLogos/microsoft.svg", testMember.getMemberLogo());
            Assert.assertEquals("Microsoft Logo", testMember.getMemberAltText());
            Assert.assertEquals("", testMember.getOrganizationType());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
