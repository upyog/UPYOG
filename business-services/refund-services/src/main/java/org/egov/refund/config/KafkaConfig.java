package org.egov.refund.config;

import org.egov.tracer.kafka.CustomKafkaTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class KafkaConfig {

    @Bean
    public CustomKafkaTemplate<String, Object> customKafkaTemplate(
            KafkaTemplate<String, Object> kafkaTemplate) {

        return new CustomKafkaTemplate<>(kafkaTemplate);
    }
}