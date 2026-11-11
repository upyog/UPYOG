package org.upyog.wms;

import org.egov.tracer.config.TracerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({TracerConfiguration.class})
public class WorksManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorksManagementApplication.class, args);
    }
}
