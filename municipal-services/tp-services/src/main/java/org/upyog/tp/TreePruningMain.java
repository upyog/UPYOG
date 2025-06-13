package org.upyog.tp;

import org.egov.tracer.config.TracerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import({ TracerConfiguration.class })
@SpringBootApplication
@ComponentScan(basePackages = { "org.upyog.tp", "org.upyog.tp.web.controllers" , "org.upyog.tp.config"})
public class TreePruningMain {


    public static void main(String[] args) throws Exception {
        SpringApplication.run(TreePruningMain.class, args);
    }

}
