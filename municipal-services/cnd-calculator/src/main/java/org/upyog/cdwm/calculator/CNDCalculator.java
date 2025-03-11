package org.upyog.cdwm.calculator;


import org.egov.tracer.config.TracerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import({ TracerConfiguration.class })
@SpringBootApplication
@ComponentScan(basePackages = { "org.upyog.cdwm.calculator", "org.upyog.cdwm.calculator.web.controllers" , "org.upyog.cdwm.calculator.config"})
public class CNDCalculator {


    public static void main(String[] args) throws Exception {
        SpringApplication.run(CNDCalculator.class, args);
    }

}
