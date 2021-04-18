package net.adoptium.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import io.quarkus.jackson.ObjectMapperCustomizer;

import javax.enterprise.inject.Instance;
import javax.inject.Singleton;


public class JacksonKotlinModule {

    @Singleton
    ObjectMapper objectMapper(Instance<ObjectMapperCustomizer> customizers) {
        ObjectMapper mapper = new ObjectMapper(); // Custom `ObjectMapper`

        // Apply all ObjectMapperCustomerizer beans (incl. Quarkus)
        for (ObjectMapperCustomizer customizer : customizers) {
            customizer.customize(mapper);
        }
        System.out.println("Hoi Module");
        return mapper.registerModule(new KotlinModule()).setSerializationInclusion(JsonInclude.Include.NON_NULL).
                registerModule(new JavaTimeModule());
    }
}
