package net.adoptium;

import net.adoptium.api.ApiService;
import net.adoptopenjdk.api.v3.models.Architecture;
import net.adoptopenjdk.api.v3.models.Binary;
import net.adoptopenjdk.api.v3.models.OperatingSystem;
import okhttp3.HttpUrl;
import okhttp3.internal.http2.Header;
import okhttp3.internal.io.FileSystem;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.BufferedSource;
import okio.Okio;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

public class IndexResourceTest {

    @Test
    public void testUserDownload() throws IOException {
        // -> before
        MockWebServer server = new MockWebServer();
        BufferedSource source = Okio.buffer(FileSystem.SYSTEM.source(new File("src/test/resources/api-staging/v3_assets_latest_11_hotspot.json")));
        String v3_assets_latest_11_hotspot = source.readUtf8();
        source.close();

        System.out.println(v3_assets_latest_11_hotspot);

        server.enqueue(new MockResponse().setHeader("Content-Type", "application/json").setBody(v3_assets_latest_11_hotspot));

        server.start();

        HttpUrl baseUrl = server.url("/v3/assets/latest/11/hotspot");

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
