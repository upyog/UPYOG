package org.egov.asset;


import org.egov.tracer.config.TracerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import({ TracerConfiguration.class })
@SpringBootApplication
@ComponentScan(basePackages = { "org.egov.asset", "org.egov.asset.web.controllers" , "org.egov.asset.config"})
public class AssetApplication {


    public static void main(String[] args) throws Exception {
        SpringApplication.run(AssetApplication.class, args);
    }

}
