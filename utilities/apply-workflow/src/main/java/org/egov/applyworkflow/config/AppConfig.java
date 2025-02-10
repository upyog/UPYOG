package org.egov.applyworkflow.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.egov.tracer.config.TracerConfiguration;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
@Import({TracerConfiguration.class})
public class AppConfig {

    @Value("${app.timezone}")
    private String timeZone;

    @Value("${workflow.context.path}")
    private String wfContextPath;

    @Value("${workflow.search.path}")
    private String searchPath;

    @Value("${workflow.create.path}")
    private String createPath;

    @Value("${workflow.update.path}")
    private String updatePath;

    @PostConstruct
    public void initialize() {
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    }

}
