package org.egov.filemgmnt.config;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
class JsonMapperConfiguration {

    private final FMConfiguration filemgmntConfig;

    @Autowired
    JsonMapperConfiguration(FMConfiguration filemgmntConfig) {
        this.filemgmntConfig = filemgmntConfig;
    }

    @PostConstruct
    void initialize() {
        TimeZone.setDefault(TimeZone.getTimeZone(filemgmntConfig.getTimeZone()));
    }

    @Bean
    public ObjectMapper objectMapper() {

        return JsonMapper.builder()
                         .addModules(new JavaTimeModule())
                         .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                         .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
//                       .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                         .defaultTimeZone(TimeZone.getTimeZone(filemgmntConfig.getTimeZone()))
                         .build();
    }

    @Bean
    @Autowired
    public MappingJackson2HttpMessageConverter jacksonConverter(ObjectMapper objectMapper) {
        return new MappingJackson2HttpMessageConverter(objectMapper);
    }
}
