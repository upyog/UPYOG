package org.egov.echallan;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.util.TimeZone;

@SpringBootApplication
@Component
@Import({ TracerConfiguration.class })
public class ChallanGenerationMain {

    @Value("${app.timezone}")
    private String timeZone;

    @Bean
    public ObjectMapper objectMapper(){
        return JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .defaultTimeZone(TimeZone.getTimeZone(timeZone))
                .build();
    }


    public static void main(String[] args) {
        SpringApplication.run(ChallanGenerationMain.class, args);
    }

}
