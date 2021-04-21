package net.adoptium;

import io.quarkus.test.junit.QuarkusTest;
import io.vertx.ext.auth.User;
import net.adoptium.api.ApiService;
import net.adoptopenjdk.api.v3.models.Architecture;
import net.adoptopenjdk.api.v3.models.Binary;
import net.adoptopenjdk.api.v3.models.OperatingSystem;
import okhttp3.HttpUrl;
import okhttp3.internal.io.FileSystem;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.BufferedSource;
import okio.Okio;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * \@QuarkusTest annotation required so JacksonKotlinModule gets initialized.
 */
@QuarkusTest
public class IndexResourceTest {

    @Test
    public void testUserDownload() throws IOException {
        // current procedure for determining the correct binary: manually searching for it based on the criteria used in getUserDownload (CTRL-F "linux")
        // NOTE: for OS.windows (which usually includes installers) the first match will be used,
        //       while OS.linux generally accepts the last one (since it's trying to find an installer)
        // NOTE: when requirements change, these tests have to be adapted by hand...
        // NOTE: UserAgentParser guarantees that, if the OS is set, Architecture is set
        HashMap<UserSystem, String> tests = new HashMap<>() {{
            put(new UserSystem(OperatingSystem.windows, Architecture.x64), "f12011de94a72e1f14c9e68ce63bdd537aab1bf51eb336eba6d6061bc307baeb");
            put(new UserSystem(OperatingSystem.windows, Architecture.x32), "440c538a95770aa4a5e2f30cda3af1e2b730f8d889b8b6ea4b5d6c070b08024a");
            put(new UserSystem(OperatingSystem.linux, Architecture.x64), "ae78aa45f84642545c01e8ef786dfd700d2226f8b12881c844d6a1f71789cb99");
            put(new UserSystem(OperatingSystem.linux, Architecture.x32), null);
            // eg. Raspberry PI
            put(new UserSystem(OperatingSystem.linux, Architecture.arm), "34908da9c200f5ef71b8766398b79fd166f8be44d87f97510667698b456c8d44");
            put(new UserSystem(OperatingSystem.linux, Architecture.aarch64), "420c5d1e5dc66b2ed7dedd30a7bdf94bfaed10d5e1b07dc579722bf60a8114a9");
            put(new UserSystem(OperatingSystem.mac, Architecture.x64), "5c9a54d3bbed00d993183dc4b7bcbc305e2e6ab1bbf48c57dea7fea6c47cb9d2");
            put(new UserSystem(OperatingSystem.mac, Architecture.aarch64), null); // TODO M1 = aarch64?
        }};

        MockWebServer server = new MockWebServer();
        BufferedSource source = Okio.buffer(FileSystem.SYSTEM.source(new File("src/test/resources/api-staging/v3_assets_latest_11_hotspot.json")));
        String v3_assets_latest_11_hotspot = source.readUtf8();
        source.close();

        // TODO improve usage of MockWebServer (eg always return same response)
        for (int i = 0; i < tests.size(); i++) {
            server.enqueue(new MockResponse().setHeader("Content-Type", "application/json").setBody(v3_assets_latest_11_hotspot));
        }

        server.start();

        HttpUrl baseUrl = server.url("/v3/assets/latest/11/hotspot");

        ApiService remoteApi = RestClientBuilder.newBuilder()
                .baseUri(baseUrl.uri())
                .build(ApiService.class);

        IndexResource r = new IndexResource(remoteApi);

        tests.forEach(((userSystem, checksum) -> {
            Binary recommended = r.getUserDownload(userSystem.getOs(), userSystem.getArch());
            if (recommended == null && checksum == null) return;
            if (recommended.getInstaller() != null) {
                assertEquals(checksum, recommended.getInstaller().getChecksum(), "client: " + userSystem);
            } else {
                assertEquals(checksum, recommended.getPackage().getChecksum(), "client: " + userSystem);
            }
        }));
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
