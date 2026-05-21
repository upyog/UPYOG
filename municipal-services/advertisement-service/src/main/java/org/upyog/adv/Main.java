package org.upyog.adv;


import java.util.TimeZone;

import org.egov.encryption.config.EncryptionConfiguration;
import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Import({ TracerConfiguration.class, EncryptionConfiguration.class })
@SpringBootApplication
@ComponentScan(basePackages = { "org.upyog.adv"})
@EnableScheduling
@EnableTransactionManagement
public class Main {

	@Value("${app.timezone}")
	private String timeZone;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Main.class, args);
	}
   
    @Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
				.setTimeZone(TimeZone.getTimeZone(timeZone))//.registerModule(new JavaTimeModule())
				//Added to resolve parsing issue of String to Local date in NotificationConsumer
				.findAndRegisterModules();
	}

	@Autowired
	public MappingJackson2HttpMessageConverter jacksonConverter(ObjectMapper objectMapper) {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setObjectMapper(objectMapper);
		return converter;
	}
}
