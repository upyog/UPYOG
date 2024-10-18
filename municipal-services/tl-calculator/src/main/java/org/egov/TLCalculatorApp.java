package org.egov;


import org.cache2k.extra.spring.SpringCache2kCacheManager;
import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableCaching
@Import({ TracerConfiguration.class })
public class TLCalculatorApp {

    @Value("${cache.expiry.masterdata.minutes:5}")
    private long masterDataCacheExpiry;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(TLCalculatorApp.class, args);
    }

    @Bean
    @Profile("!test")
    public CacheManager cacheManager() {
        return new SpringCache2kCacheManager()
                .addCaches(b->b.name("mdmsData").expireAfterWrite(masterDataCacheExpiry, TimeUnit.MINUTES).entryCapacity(50))
                .addCaches(b->b.name("mdmsCache").expireAfterWrite(masterDataCacheExpiry, TimeUnit.MINUTES).entryCapacity(50));
    }


}
