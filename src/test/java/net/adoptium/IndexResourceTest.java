package net.adoptium;

import net.adoptium.api.ApiService;
import net.adoptopenjdk.api.v3.models.Architecture;
import net.adoptopenjdk.api.v3.models.Binary;
import net.adoptopenjdk.api.v3.models.OperatingSystem;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

public class IndexResourceTest {

    @Test
    public void testUserDownload() throws IOException {
        // -> before
        MockWebServer server = new MockWebServer();

        server.enqueue(new MockResponse().setBody("<openjdk api response>"));

        server.start();

        HttpUrl baseUrl = server.url("/v1/chat/");

        ApiService remoteApi = RestClientBuilder.newBuilder()
                .baseUri(baseUrl.uri())
                .build(ApiService.class);

        IndexResource r = new IndexResource(null, remoteApi);
        Binary b = r.getUserDownload(OperatingSystem.linux, Architecture.x64);
        System.out.println("b: " + b);
    }

    @Test
    public void testHelloEndpointEnglish() {
        given().header("Accept-Language", "en-US") //chrome and edge sends with a "-"
                .when().get("/")
                .then()
                .statusCode(200)
                .body(containsString("<p class=\"lead\">Hello</p>"));
    }

    @Test
    public void testHelloEndpointGerman() {
        given().header("Accept-Language", "de,en-US;q=0.7,en;q=0.3") //chrome and edge sends with a "-"
                .when().get("/")
                .then()
                .statusCode(200)
                .body(containsString("<p class=\"lead\">Hallo</p>"));
    }
}
