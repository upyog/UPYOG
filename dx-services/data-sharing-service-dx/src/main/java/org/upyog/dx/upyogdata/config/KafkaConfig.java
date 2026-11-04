package org.upyog.dx.upyogdata.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@Getter
public class KafkaConfig {

    @Value("${upyog.data.kafka.topic}")
    private String topic;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
