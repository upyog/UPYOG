package org.egov.edcr.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for Jackson-related beans.
 * <p>
 * Defines and exposes a shared {@link ObjectMapper} bean that can be
 * injected and reused throughout the application for JSON serialization
 * and deserialization.
 * </p>
 */
@Configuration
public class JacksonConfig {

    /**
     * Creates the default Jackson {@link ObjectMapper} instance.
     *
     * @return configured ObjectMapper bean
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
//        mapper.setVisibility(
//                PropertyAccessor.FIELD,
//                JsonAutoDetect.Visibility.ANY);
//        return mapper;
    }
}
