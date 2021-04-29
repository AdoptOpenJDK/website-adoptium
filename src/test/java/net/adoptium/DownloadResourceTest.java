package net.adoptium;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

@QuarkusTest
public class DownloadResourceTest {

    OkHttpClient client = new OkHttpClient();

    @TestHTTPEndpoint(DownloadResource.class)
    @TestHTTPResource
    URL downloadUrl;

    @Test
    public void testDownloadLink() throws IOException {
        Request request = new Request.Builder().url(new URL(downloadUrl.toString() + "/thank-you/windows-x64-hotspot-jdk-normal-jdk-ga-adoptopenjdk-11.0.10+9")).build();
        Response response = client.newCall(request).execute();
        ResponseBody body = response.body();

        assert body != null;
        Assertions.assertEquals(200, response.code());

        Assertions.assertTrue(body.string().contains("https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/download/jdk-11.0.10%2B9/OpenJDK11U-jdk_x64_windows_hotspot_11.0.10_9.msi"));
    }

    @Test
    public void testArgParsingMissingArg() throws IOException {
        Request request = new Request.Builder().url(new URL(downloadUrl.toString() + "/thank-you/windows-x64-hotspot-jdk-jdk-ga-adoptopenjdk-11.0.10+9")).build();
        Response response = client.newCall(request).execute();
        ResponseBody body = response.body();

        assert body != null;
        Assertions.assertEquals(404, response.code());
    }

    @Test
    public void testArgParsingInvalidVersion() throws IOException {
        Request request = new Request.Builder().url(new URL(downloadUrl.toString() + "/download/thank-you/windows-x64-hotspot-jdk-normal-jdk-ga-adoptopenjdk-11.0.0.0.0.0.0")).build();
        Response response = client.newCall(request).execute();
        ResponseBody body = response.body();

        assert body != null;
        Assertions.assertEquals(404, response.code());
    }

}