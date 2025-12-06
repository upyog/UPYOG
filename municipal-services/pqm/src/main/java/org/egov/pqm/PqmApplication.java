package org.egov.pqm;

import org.egov.tracer.config.TracerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import({TracerConfiguration.class})
@SpringBootApplication
@ComponentScan(basePackages = {"org.egov"})
public class PqmApplication {

    public static void main(String[] args) {
        SpringApplication.run(PqmApplication.class, args);
    }

}
