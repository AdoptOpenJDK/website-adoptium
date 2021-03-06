package net.adoptium;

import com.microsoft.playwright.*;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import net.adoptium.model.Member;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MemberPageTest {

    private Browser browser;
    private Page page;
    private MemberResource memberResource;

    @TestHTTPEndpoint(MemberResource.class)
    @TestHTTPResource
    URL memberURL;

    @BeforeAll
    void setup() {
        setUpBrowser();

        setUpMemberResource();
    }

    private void setUpBrowser() {
        Playwright playwright = Playwright.create();

        BrowserType browserType = playwright.firefox();
        browser = browserType.launch();
        BrowserContext context = browser.newContext();
        page = context.newPage();
        page.navigate(memberURL.toString());
    }

    private void setUpMemberResource() {
        memberResource = new MemberResource();
    }

    @AfterAll
    void close() {
        browser.close();
    }

    @Test
    void testIfMembersLoad() {
        String strategicDiv = "strategic";
        String strategicInnerHTML = page.querySelector(String.format(".%s", strategicDiv)).innerHTML();
        for (Member member : memberResource.getStrategicMembers()) {
            assertTrue(strategicInnerHTML.contains(member.getMemberAltText()));
        }
        String enterpriseDiv = "enterprise";
        String enterpriseInnerHTML = page.querySelector(String.format(".%s", enterpriseDiv)).innerHTML();
        for (Member member : memberResource.getEnterpriseMembers()) {
            assertTrue(enterpriseInnerHTML.contains(member.getMemberAltText()));
        }
        String participantDiv = "participant";
        String participantInnerHTML = page.querySelector(String.format(".%s", participantDiv)).innerHTML();
        for (Member member : memberResource.getParticipantMembers()) {
            assertTrue(participantInnerHTML.contains(member.getMemberAltText()));
        }
    }

}
