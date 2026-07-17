package org.upyog.extractor;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@SpringBootApplication(scanBasePackages = { "org.upyog.extractor", "org.upyog.adapter" })
@Import({ TracerConfiguration.class })
@EnableScheduling  // <-- Crucial! Enable scheduling detection here
public class DataExtractorApplication {

	@Value("${kafka.config.bootstrap_server_config:localhost:9092}")
	private String bootstrapServers;

	public static void main(String[] args) {
		SpringApplication.run(DataExtractorApplication.class, args);
	}

	// Define the missing RestTemplate bean here
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

	@Bean
	public Gson gson() {
		return new Gson();
	}

	// 1. Define the ProducerFactory using your properties
	@Bean
	public ProducerFactory<String, Object> producerFactory() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return new DefaultKafkaProducerFactory<>(configProps);
	}

	// 2. Define the explicit KafkaTemplate that CustomKafkaTemplate is asking for
	@Bean
	public KafkaTemplate<String, Object> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}

}
