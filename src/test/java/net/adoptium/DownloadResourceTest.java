package net.adoptium;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import net.adoptium.api.DownloadRepository;
import net.adoptium.exceptions.DownloadBinaryNotFoundException;
import net.adoptium.utils.DownloadArgumentGroup;
import net.adoptium.utils.DownloadStringArgumentExtractor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class DownloadResourceTest {

    OkHttpClient client = new OkHttpClient();

    @TestHTTPEndpoint(DownloadResource.class)
    @TestHTTPResource
    URL downloadURL;

    @InjectMock
    DownloadRepository mockRepository;

    @Test
    void testBinaryNotFoundException() throws IOException {
        String args = "windows-x64-hotspot-jdk-normal-jdk-ga-adoptopenjdk-11.0.10+9";
        Map<DownloadArgumentGroup, String> expectedVersionDetails = DownloadStringArgumentExtractor.getVersionDetails(args);

        // empty list -> API has no matching binary
        Mockito.when(mockRepository.getBinary(expectedVersionDetails)).thenThrow(new DownloadBinaryNotFoundException());

        Request request = new Request.Builder()
                .url(downloadURL.toString() + "/thank-you/" + args)
                .build();
        Response response = client.newCall(request).execute();
        ResponseBody body = response.body();

        assertThat(body).isNotNull();
        assertThat(response.code()).isEqualTo(404);

        // exceptionDownloadNotFound
        assertThat(body.string()).contains("Download not found");
    }

    @Test
    void testInvalidArgumentException() throws IOException {
        String args = "bad-args";

        Request request = new Request.Builder()
                .url(downloadURL.toString() + "/thank-you/" + args)
                .build();
        Response response = client.newCall(request).execute();
        ResponseBody body = response.body();

        assertThat(body).isNotNull();
        assertThat(response.code()).isEqualTo(404);

        // exceptionVersionNotFound
        assertThat(body.string()).contains("Version not found");
    }
}
