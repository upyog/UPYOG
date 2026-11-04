package org.upyog.dashboard.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import org.upyog.dashboard.constants.DashboardExtractorConstants;

/**
 * General application configuration providing common beans like {@link RestTemplate} and {@link CacheManager}.
 */
@Configuration
public class AppConfig {

    /**
     * Creates a {@link RestTemplate} bean for HTTP communications with external services.
     *
     * @return configured {@link RestTemplate} instance
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Configures an in-memory {@link CacheManager} for tenant and module lookup caching.
     *
     * @return configured {@link CacheManager} instance
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                DashboardExtractorConstants.CACHE_ACTIVE_TENANTS,
                DashboardExtractorConstants.CACHE_TENANT_MODULE_DETAILS
        );
    }
}
