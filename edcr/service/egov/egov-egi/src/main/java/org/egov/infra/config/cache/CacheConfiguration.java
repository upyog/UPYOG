package org.egov.infra.config.cache;

import org.egov.infra.config.cache.resolver.MultiTenantCacheResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import jakarta.annotation.Resource;
import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Cache configuration for the application.
 *
 * <p>Note (Spring 6 Migration):
 * {@code CachingConfigurerSupport} is deprecated in Spring Framework 6 in favor of directly
 * implementing the {@link CachingConfigurer} interface. Since {@link CachingConfigurer} defines
 * default methods for all configuration hooks, extending an adapter class is no longer necessary.
 */
@Configuration
@EnableCaching(proxyTargetClass = true)
@DependsOn("applicationConfiguration")
public class CacheConfiguration implements CachingConfigurer {

    @Autowired
    private RedisTemplate redisTemplate;
    private List<String> cities;

    /**
     * Creates and configures the multi-tenant cache resolver.
     *
     * @return the {@link CacheResolver} resolving cache instances per tenant
     */
    @Bean
    @Override
    public CacheResolver cacheResolver() {
        return new MultiTenantCacheResolver(cacheManager());
    }

    /**
     * Creates a custom {@link KeyGenerator} that generates cache keys by combining
     * the target class name, method name, and method arguments.
     *
     * @return the configured {@link KeyGenerator}
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (object, method, args) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(object.getClass().getSimpleName());
            sb.append(method.getName());
            for (Object obj : args) {
                sb.append(obj);
            }
            return sb.toString();
        };
    }

    /**
     * Creates and configures the Redis-based cache manager used by the application.
     * <p>
     * The cache manager is configured with:
     * <ul>
     *     <li>A default cache entry time-to-live (TTL) of one hour.</li>
     *     <li>Cache key prefixes to avoid key collisions between caches.</li>
     *     <li>Transaction-aware cache operations.</li>
     *     <li>Predefined cache names loaded from the configured cities list.</li>
     * </ul>
     * </p>
     *
     * @return the configured {@link CacheManager} instance
     */
    @Bean
    @Override
    public CacheManager cacheManager() {
        RedisCacheConfiguration cacheConfig =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofHours(1))
                        .computePrefixWith(CacheKeyPrefix.simple());

        return RedisCacheManager.builder(redisTemplate.getConnectionFactory())
                .cacheDefaults(cacheConfig)
                .transactionAware()
                .initialCacheNames(cities == null ? Collections.emptySet() : new HashSet<>(cities))
                .build();
    }

    @Resource(name = "cities")
    public void setCities(List<String> cities) {
        this.cities = cities;
    }

}
