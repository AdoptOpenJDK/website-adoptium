package net.adoptium.api;

import okhttp3.internal.io.FileSystem;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import okio.BufferedSource;
import okio.Okio;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ApiMockServer extends Dispatcher {

    private static final Logger LOG = Logger.getLogger(ApiMockServer.class);

    private final HashMap<String, String> responses = new HashMap<>();

    public ApiMockServer() throws IOException {
        LOG.infof("loading assets...");

        File mockResponsesDir = new File("src/test/resources/api-staging");

        // v3_assets_latest_11_hotspot -> v3/assets/latest/11/hotspot
        for (String path : mockResponsesDir.list((file, s) -> s.endsWith(".json"))) {
            BufferedSource source = Okio.buffer(FileSystem.SYSTEM.source(new File(mockResponsesDir, path)));
            String url = "/" + path.replace('_', '/').split(".json")[0];

            LOG.infof("registering response '%s' as '%s'...", path, url);
            responses.put(url, source.readUtf8());
            source.close();
        }
    }

    @Override
    public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
        if (responses.containsKey(request.getPath())) {
            return new MockResponse().setHeader("Content-Type",
                    "application/json").setBody(responses.get(request.getPath()));
        }
        return new MockResponse().setResponseCode(404);
    }
}
