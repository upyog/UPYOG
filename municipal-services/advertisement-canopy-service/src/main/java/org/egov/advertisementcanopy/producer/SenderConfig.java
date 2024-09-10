package org.egov.advertisementcanopy.producer;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Configuration
public class SenderConfig {

	@Value("${kafka.config.bootstrap_server_config}")
	private String bootstrapServers;

	@Bean
	public Map<String, Object> producerConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 10000000);
		return props;
	}

	@Bean
	public ProducerFactory<String, Object> producerFactory() {
		return new DefaultKafkaProducerFactory<>(producerConfigs());
	}

	@Bean
	public KafkaTemplate<String, Object> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}
	
	
	
	

	@Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> config = new HashMap<>();
        config.put("bootstrap.servers", bootstrapServers);
        return new KafkaAdmin(config);
    }
    
	@Bean
    public AdminClient kafkaAdminClient() {
        return AdminClient.create(kafkaAdmin().getConfigurationProperties());
    }

	@Bean
    public void createTopicsIfNotExist() throws ExecutionException, InterruptedException {
        AdminClient adminClient = kafkaAdminClient();

        String[] topics = {"save-site-booking", "update-site-booking", "create-site", "update-site"};

        for (String topic : topics) {
            Map<String, TopicListing> topicListingMap = adminClient.listTopics().namesToListings().get();
            if (!topicListingMap.containsKey(topic)) {
                NewTopic newTopic = new NewTopic(topic, 1, (short) 1); // 1 partition, 1 replication factor
                adminClient.createTopics(Collections.singleton(newTopic)).all().get();
                System.out.println("Created Kafka topic: " + topic);
            } else {
                System.out.println("Kafka topic already exists: " + topic);
            }
        }

        adminClient.close();
    }

}
