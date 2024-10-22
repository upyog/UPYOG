package org.upyog.sv;


import org.egov.tracer.config.TracerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import({ TracerConfiguration.class })
@SpringBootApplication
@ComponentScan(basePackages = { "org.upyog.sv", "org.upyog.sv.web.controllers" , "org.upyog.sv.config"})
public class StreetVendingApplication {


    public static void main(String[] args) throws Exception {
        SpringApplication.run(StreetVendingApplication.class, args);
    }

}
