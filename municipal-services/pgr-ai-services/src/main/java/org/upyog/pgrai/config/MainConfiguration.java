package org.upyog.pgrai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.tracer.config.TracerConfiguration;

/**
 * Main configuration class for the application.
 * Configures the application timezone, object mapper, and message converters.
 */
@Import({TracerConfiguration.class})
public class MainConfiguration {

    @Value("${app.timezone}")
    private String timeZone;

    /**
     * Initializes the application with the configured timezone.
     * Sets the default timezone for the application.
     */
    @PostConstruct
    public void initialize() {
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    }

    /**
     * Configures and provides a customized {@link ObjectMapper} bean.
     *
     * @return The configured {@link ObjectMapper}.
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .setTimeZone(TimeZone.getTimeZone(timeZone));
    }

    /**
     * Configures and provides a {@link MappingJackson2HttpMessageConverter} bean.
     *
     * @param objectMapper The {@link ObjectMapper} to be used by the converter.
     * @return The configured {@link MappingJackson2HttpMessageConverter}.
     */
    @Bean
    @Autowired
    public MappingJackson2HttpMessageConverter jacksonConverter(ObjectMapper objectMapper) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        return converter;
    }
}