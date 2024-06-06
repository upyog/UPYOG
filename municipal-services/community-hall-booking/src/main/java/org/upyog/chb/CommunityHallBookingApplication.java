package org.upyog.chb;


import org.egov.tracer.config.TracerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import({ TracerConfiguration.class })
@SpringBootApplication
@ComponentScan(basePackages = { "org.upyog.chb", "org.upyog.chb.web.controllers" , "org.upyog.chb.config"})
public class CommunityHallBookingApplication {


    public static void main(String[] args) throws Exception {
        SpringApplication.run(CommunityHallBookingApplication.class, args);
    }
    
  

}
