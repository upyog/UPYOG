package org.egov.gis.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@Configuration
@Import({TracerConfiguration.class})
public class MainConfiguration {

    @Value("${app.timezone}")
    private String timeZone;

    @PostConstruct
    public void initialize() {
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    }

    @Bean
    @Autowired
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        ObjectMapper customMapper = new ObjectMapper();
        customMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        customMapper.setTimeZone(TimeZone.getTimeZone(timeZone));

        customMapper.disable(com.fasterxml.jackson.databind.MapperFeature.USE_ANNOTATIONS);
        customMapper.disable(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(customMapper);
        restTemplate.getMessageConverters().removeIf(c -> c instanceof MappingJackson2HttpMessageConverter);
        restTemplate.getMessageConverters().add(0, converter);
        
        return restTemplate;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .setTimeZone(TimeZone.getTimeZone(timeZone));
    }
}

