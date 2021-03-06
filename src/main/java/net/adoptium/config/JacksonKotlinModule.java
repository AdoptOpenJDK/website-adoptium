package net.adoptium.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import io.quarkus.jackson.ObjectMapperCustomizer;
import org.jboss.logging.Logger;

import javax.enterprise.inject.Instance;
import javax.inject.Singleton;

/**
 * JacksonKotlinModule configures Jackson to handle openjdk api models correctly.
 * Those models are written in Kotlin so we need to inject KotlinModule.
 * Additionally, openjdk api models use certain other modules,
 * like JavaTimeModule, which we need for formatting compatibility.
 */
public class JacksonKotlinModule {

    private static final Logger LOG = Logger.getLogger(JacksonKotlinModule.class);

    @Singleton
    public ObjectMapper objectMapper(Instance<ObjectMapperCustomizer> customizers) {
        ObjectMapper mapper = new ObjectMapper(); // Custom `ObjectMapper`

        // Apply all ObjectMapperCustomizer beans (incl. Quarkus)
        for (ObjectMapperCustomizer customizer : customizers) {
            customizer.customize(mapper);
        }

        LOG.info("injecting KotlinModule...");
        return mapper
                .registerModule(new KotlinModule())
                .registerModule(new JavaTimeModule())
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}
