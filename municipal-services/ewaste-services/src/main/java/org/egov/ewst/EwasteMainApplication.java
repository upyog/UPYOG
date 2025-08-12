package org.egov.ewst;

import java.util.TimeZone;

import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Main application class for the e-waste service.
 * This class initializes the Spring Boot application and configures necessary beans.
 */
@SpringBootApplication
@ComponentScan(basePackages = { "org.egov.ewst", "org.egov.ewst.web.controllers", "org.egov.ewst.config",
		"org.egov.ewst.repository" })
@Import({ TracerConfiguration.class })
public class EwasteMainApplication {

	// Timezone configuration property
	@Value("${app.timezone}")
	private String timeZone;

	/**
	 * Configures the ObjectMapper bean for JSON serialization and deserialization.
	 *
	 * @return Configured ObjectMapper instance.
	 */
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper()
				.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
				.setTimeZone(TimeZone.getTimeZone(timeZone));
	}

	/**
	 * Main method to start the Spring Boot application.
	 *
	 * @param args Command-line arguments.
	 * @throws Exception If an error occurs during application startup.
	 */
	public static void main(String[] args) throws Exception {
		// Disable Spring DevTools restart feature
		System.setProperty("spring.devtools.restart.enabled", "false");

		// Run the Spring Boot application
		SpringApplication.run(EwasteMainApplication.class, args);
	}
}