package org.egov.applyworkflow;

import org.egov.tracer.config.TracerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ TracerConfiguration.class })
@ComponentScan(basePackages = { "org.egov.applyworkflow", "org.egov.applyworkflow.web.controllers" , "org.egov.applyworkflow.config"})
public class ApplyWorkflowApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApplyWorkflowApplication.class, args);
    }
}
