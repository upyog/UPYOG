package org.egov.vendor;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.egov.vendor.web.models.VendorAdditionalDetailsRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@SpringBootApplication
@ComponentScan(basePackages = {"org.egov.vendor", "org.egov.vendor.web.controllers", "org.egov.vendor.config", "org.egov.vendor.kafka"})
public class VendorManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(VendorManagementApplication.class, args);
    }

}
