package org.egov.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Spring configuration for RestTemplate bean creation.
 * <p>
 * Defines a shared {@link RestTemplate} bean that can be injected and
 * reused across the application for making REST API calls.
 * </p>
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Creates and exposes a RestTemplate bean.
     *
     * @return RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
