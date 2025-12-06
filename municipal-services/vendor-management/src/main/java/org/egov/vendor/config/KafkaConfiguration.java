package org.egov.vendor.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfiguration {

    private final VendorConfiguration vendorConfiguration;

    public KafkaConfiguration(org.egov.vendor.config.VendorConfiguration vendorConfiguration) {
        this.vendorConfiguration = vendorConfiguration;
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        JsonDeserializer<Object> deserializer = new JsonDeserializer<>(Object.class);
        deserializer.addTrustedPackages("*");

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, vendorConfiguration.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, vendorConfiguration.getGroupId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, vendorConfiguration.getKeyDeserializerClass());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, vendorConfiguration.getValueDeserializerClass());

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }


//    @Bean
//    public ConsumerFactory<String, VendorAdditionalDetailsRequest> consumerFactory() {
//        JsonDeserializer<VendorAdditionalDetailsRequest> deserializer = new JsonDeserializer<>(VendorAdditionalDetailsRequest.class);
//        deserializer.addTrustedPackages("*");
//
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, "vendor-group");
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
//
//        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, VendorAdditionalDetailsRequest> kafkaListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, VendorAdditionalDetailsRequest> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory());
//        return factory;
//    }

}
