package org.egov.user;

import org.egov.encryption.EncryptionService;
import org.egov.user.util.CookieUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.mock;

@Configuration
public class TestConfiguration {

    @Bean
    @SuppressWarnings("unchecked")
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return mock(KafkaTemplate.class);
    }

    @Bean
    public CookieUtil cookieUtil() {
        return mock(CookieUtil.class);
    }

    @Bean
    public EncryptionService encryptionService() {
        return mock(EncryptionService.class);
    }

}
