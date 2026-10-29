package org.egov.applyworkflow.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
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
