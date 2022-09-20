package org.egov.filemgmnt;

import org.egov.tracer.config.TracerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import({ TracerConfiguration.class })
@SpringBootApplication
@ComponentScan(basePackages = { "org.egov.filemgmnt", "org.egov.filemgmnt.web.controllers",
        "org.egov.filemgmnt.config" })
public class Main { // NOPMD

    public static void main(String[] args) { // NOPMD
        SpringApplication.run(Main.class, args);
    }

}
