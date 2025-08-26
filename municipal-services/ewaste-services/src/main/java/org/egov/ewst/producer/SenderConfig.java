package org.egov.ewst.producer;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for Kafka producer.
 * This class defines the necessary beans and configurations required for sending messages to Kafka topics.
 */
@Configuration
public class SenderConfig {

	// Kafka bootstrap server configuration
	@Value("${kafka.config.bootstrap_server_config}")
	private String bootstrapServers;

	/**
	 * Configures the producer properties for Kafka.
	 *
	 * @return A map containing the producer configurations.
	 */
	@Bean
	public Map<String, Object> producerConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers); // Kafka server address
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class); // Key serializer
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class); // Value serializer
		props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 10000000); // Maximum request size
		return props;
	}

	/**
	 * Creates a producer factory for Kafka.
	 *
	 * @return A ProducerFactory instance.
	 */
	@Bean
	public ProducerFactory<String, Object> producerFactory() {
		return new DefaultKafkaProducerFactory<>(producerConfigs());
	}

	/**
	 * Creates a KafkaTemplate for sending messages.
	 *
	 * @return A KafkaTemplate instance.
	 */
	@Bean
	public KafkaTemplate<String, Object> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}
}