package org.upyog.pgrai;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.utils.MultiStateInstanceUtil;
import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.util.TimeZone;

/**
 * Main application class for the PGR (Public Grievance Redressal AI) module.
 *
 * Key features of this class:
 * - Configures the application timezone based on the `app.timezone` property.
 * - Customizes the `ObjectMapper` to handle case-insensitive properties and unknown properties during deserialization.
 * - Imports additional configurations such as `TracerConfiguration` and `MultiStateInstanceUtil`.
 *
 * This class is the entry point for the Spring Boot application.
 */
@EnableFeignClients
@SpringBootApplication
@ComponentScan(basePackages = { "org.upyog.pgrai", "org.upyog.pgrai.web.controllers" , "org.upyog.pgrai.config"})
@Import({TracerConfiguration.class, MultiStateInstanceUtil.class})
public class PGRAiApp {

    @Value("${app.timezone}")
    private String timeZone;

    /**
     * Configures and returns a customized `ObjectMapper` bean.
     * The `ObjectMapper` is configured to:
     * - Accept case-insensitive properties.
     * - Ignore unknown properties during deserialization.
     * - Use the application timezone for date/time processing.
     *
     * @return a customized `ObjectMapper` instance
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .setTimeZone(TimeZone.getTimeZone(timeZone));
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(PGRAiApp.class, args);
    }
}
