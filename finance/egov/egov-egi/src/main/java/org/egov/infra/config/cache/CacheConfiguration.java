/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */

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
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.core.RedisTemplate;

import jakarta.annotation.Resource;
import java.util.List;


/*
 * LTS Migration Fix (Spring Data Redis / Spring Session):
 * Updated Redis cache configuration as part of the LTS migration and the upgrade
 * of Spring Data Redis/Spring Session Redis dependencies.
 *
 * The legacy RedisCacheManager constructor and setter-based APIs have been
 * replaced with the builder-based configuration required by the upgraded
 * Spring Data Redis version.
 *
 * Existing cache configuration is retained by:
 * - Keeping transaction-aware caching enabled.
 * - Initializing the existing city-based cache names.
 * - Maintaining the existing 1-hour cache expiration using Duration.
 *
 */


/**
 * Redis-backed Spring Cache configuration for the multi-tenant ERP.
 * <p>
 * Spring 6 deprecated {@code CachingConfigurerSupport}; this class implements
 * {@link CachingConfigurer} directly. Cache names are tenant/city specific so
 * that cached values are not shared across schemas.
 * </p>
 */
@Configuration
@EnableCaching(proxyTargetClass = true)
@DependsOn("applicationConfiguration")
public class CacheConfiguration implements CachingConfigurer {

    @Autowired
    private RedisTemplate redisTemplate;
    private List<String> cities;

    /**
     * Resolves cache names per tenant so Redis keys are not shared across schemas.
     *
     * @return multi-tenant cache resolver
     */
    @Bean
    @Override
    public CacheResolver cacheResolver() {
        return new MultiTenantCacheResolver(cacheManager());
    }

    /**
     * Builds a cache key from class name, method name, and argument values.
     *
     * @return cache key generator
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
     * Transaction-aware Redis cache manager with a 1-hour TTL and one cache region
     * per configured city/tenant.
     *
     * @return Redis cache manager
     */
    @Bean
    @Override
    public CacheManager cacheManager() {
        RedisCacheWriter cacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisTemplate.getConnectionFactory());
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(java.time.Duration.ofSeconds(60 * 60L))
            .disableCachingNullValues();

        return RedisCacheManager.builder(cacheWriter)
            .cacheDefaults(cacheConfig)
            .transactionAware()
            .initialCacheNames(new java.util.HashSet<>(cities))
            .build();
    }

    @Resource(name = "cities")
    public void setCities(List<String> cities) {
        this.cities = cities;
    }

}
