package org.egov.user.config;

import org.egov.user.util.CookieUtil;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Test configuration to provide mock beans for testing
 * This ensures tests don't fail due to missing CookieUtil bean
 */
@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public CookieUtil cookieUtil() {
        return new CookieUtil();
    }
}
