package net.adoptium;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
public class DownloadResourceTest {

    @Test
    public void testDownloadLink() {
        given().header("Accept-Language", "en-US").when().get("/download/thank-you/windows-x64-hotspot-jdk-normal-jdk-ga-adoptopenjdk-11.0.10+9")
                .then()
                .statusCode(200)
                .body(containsString("<a href=\"https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/download/"));
    }

    @Test
    public void testArgParsingGood() {
        given().when().get("/download/thank-you/windows-x64-hotspot-jdk-normal-jdk-ga-adoptopenjdk-11.0.10+9")
                .then()
                .statusCode(200);
    }

    /*@Test
    public void testArgParsingMissingArg() {
        given().when().get("/download/thank-you/windows-x64-hotspot-jdk-jdk-ga-adoptopenjdk-11.0.10+9")
                .then()
                .statusCode(404);
    }*/

    @Test
    public void testArgParsingInvalidVersion() {
        given().when().get("/download/thank-you/windows-x64-hotspot-jdk-normal-jdk-ga-adoptopenjdk-11.0.0.0.0.0.0")
                .then()
                .statusCode(404);
    }

}