package org.upyog.chb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
<<<<<<< HEAD
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@Profile("test")
public class TestConfiguration {

    @Bean
    @SuppressWarnings("unchecked")
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(null);
    }
}
=======
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.mock;

@Configuration
public class TestConfiguration {
    @Bean
    @SuppressWarnings("unchecked")
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return mock(KafkaTemplate.class);
    }
}
>>>>>>> master-LTS
