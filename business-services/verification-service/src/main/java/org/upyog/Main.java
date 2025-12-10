package org.upyog;


import org.egov.tracer.config.TracerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import lombok.extern.slf4j.Slf4j;

@Import({ TracerConfiguration.class })
@SpringBootApplication
@Slf4j
@ComponentScan(basePackages = { "org.upyog", "org.upyog.web.controllers" , "org.upyog.config"})
public class Main {


    public static void main(String[] args) throws Exception {
        log.info("Verification Service is running with latest LTS upgrades 2.0.0!");
        SpringApplication.run(Main.class, args);
    }

}
