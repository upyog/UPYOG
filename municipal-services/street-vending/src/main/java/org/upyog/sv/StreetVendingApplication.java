package org.upyog.sv;


import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.egov.encryption.config.EncryptionConfiguration;
import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Import({ TracerConfiguration.class, EncryptionConfiguration.class })
@SpringBootApplication
@ComponentScan(basePackages = { "org.upyog.sv", "org.upyog.sv.web.controllers" , "org.upyog.sv.config"})
public class StreetVendingApplication {


	@Value("${app.timezone}")
	private String timeZone;
	
    public static void main(String[] args) throws Exception {
        SpringApplication.run(StreetVendingApplication.class, args);
    }
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setTimeZone(TimeZone.getTimeZone(timeZone));
        mapper.registerModule(new JavaTimeModule());
        mapper.findAndRegisterModules();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        return mapper;
       
    }

    @Bean
    public MappingJackson2HttpMessageConverter jacksonConverter(ObjectMapper objectMapper) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        return converter;
    }

}
