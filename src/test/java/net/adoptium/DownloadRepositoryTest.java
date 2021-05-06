package net.adoptium;

import io.quarkus.test.junit.QuarkusTest;
import net.adoptium.api.ApiMockServer;
import net.adoptium.api.ApiService;
import net.adoptium.api.DownloadRepository;
import net.adoptium.model.Download;
import net.adoptium.model.UserSystem;
import net.adoptopenjdk.api.v3.models.Architecture;
import net.adoptopenjdk.api.v3.models.Binary;
import net.adoptopenjdk.api.v3.models.OperatingSystem;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockWebServer;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * \@QuarkusTest annotation required so JacksonKotlinModule gets initialized.
 */
@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DownloadRepositoryTest {

    /**
     * mockWebServer mocks the api by returning responses from json files in test/resources/api-stating
     */
    private MockWebServer mockWebServer;
    private ApiService remoteApi;

    @BeforeAll
    public void setupMockServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.setDispatcher(new ApiMockServer());
        mockWebServer.start();

        // all paths are registered as absolute paths
        HttpUrl baseUrl = mockWebServer.url("/");

        remoteApi = RestClientBuilder.newBuilder()
                .baseUri(baseUrl.uri())
                .build(ApiService.class);
    }

    @Test
    public void testUserDownload() {
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

        DownloadRepository repository = new DownloadRepository(remoteApi);

        tests.forEach((userSystem, checksum) -> {
            Download recommended = repository.getUserDownload(userSystem.getOs(), userSystem.getArch());
            if (recommended == null && checksum == null) return;

            Binary recommendedBinary = recommended.getBinary();
            if (recommendedBinary.getInstaller() != null) {
                assertThat(checksum).overridingErrorMessage("client: " + userSystem).isEqualTo(recommendedBinary.getInstaller().getChecksum());
            } else {
                assertThat(checksum).overridingErrorMessage("client: " + userSystem).isEqualTo(recommendedBinary.getPackage().getChecksum());
            }
        });
    }

    @AfterAll
    public void shutdownMockServer() throws IOException {
        mockWebServer.shutdown();
    }
}
