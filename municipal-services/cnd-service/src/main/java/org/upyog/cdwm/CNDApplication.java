package org.upyog.cdwm;


import org.egov.tracer.config.TracerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import({ TracerConfiguration.class })
@SpringBootApplication
@ComponentScan(basePackages = { "org.upyog.cdwm", "org.upyog.cdwm.web.controllers" , "org.upyog.cdwm.config"})
public class CNDApplication {


    public static void main(String[] args) throws Exception {
        SpringApplication.run(CNDApplication.class, args);
    }

}
