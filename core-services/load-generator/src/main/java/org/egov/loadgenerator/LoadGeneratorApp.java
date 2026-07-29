package org.egov.loadgenerator;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.TimeZone;

/**
 * Entry point for the Load Generator Spring Boot application.
 *
 * <p>This class bootstraps the application, enables component scanning,
 * configures asynchronous execution, imports common tracing
 * configuration, and registers shared infrastructure beans used
 * throughout the application.
 *
 * <h3>Responsibilities</h3>
 * <ul>
 *   <li>Initialize the Spring Boot application context.</li>
 *   <li>Enable asynchronous task execution.</li>
 *   <li>Register the shared {@link ObjectMapper} bean.</li>
 *   <li>Import distributed tracing configuration.</li>
 * </ul>
 *
 * @see ObjectMapper
 * @see TracerConfiguration
 */
@SpringBootApplication
@ComponentScan(basePackages = {"org.egov.loadgenerator"})
@Import({TracerConfiguration.class})
@EnableAsync
public class LoadGeneratorApp {

    @Value("${app.timezone}")
    private String timeZone;

    /**
     * Creates and configures the shared {@link ObjectMapper}
     * used throughout the application.
     *
     * <p>The mapper is configured to support case-insensitive
     * property mapping, ignore unknown JSON properties during
     * deserialization, and use the application-configured
     * default time zone.
     *
     * @return the configured ObjectMapper instance
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .setTimeZone(TimeZone.getTimeZone(timeZone));
    }

    /**
     * Starts the Load Generator Spring Boot application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(LoadGeneratorApp.class, args);
    }
}
