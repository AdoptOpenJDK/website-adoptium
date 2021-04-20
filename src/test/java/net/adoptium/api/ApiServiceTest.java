package net.adoptium.api;

import net.adoptopenjdk.api.v3.models.BinaryAssetView;
import net.adoptopenjdk.api.v3.models.JvmImpl;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import java.util.ArrayList;
import java.util.List;

/*@Alternative()
@Priority(1)
@ApplicationScoped
@RestClient
public class ApiServiceTest implements ApiService {

    @Override
    public List<BinaryAssetView> getAvailableReleases(int featureVersion, JvmImpl jvmImpl) {
        return new ArrayList<>(

        );
    }
}*/

