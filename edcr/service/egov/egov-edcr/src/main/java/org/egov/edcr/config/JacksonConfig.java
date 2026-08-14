package org.egov.edcr.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for Jackson JSON serialization beans.
 *
 * <p>Provides an application-scoped {@link ObjectMapper} bean. WildFly-bundled Jackson
 * modules are excluded in {@code jboss-deployment-structure.xml} so this bean uses
 * the application-packaged Jackson 2.13.5 libraries, avoiding version conflicts
 * with the server's RestEasy Jackson provider on WildFly 26.</p>
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
