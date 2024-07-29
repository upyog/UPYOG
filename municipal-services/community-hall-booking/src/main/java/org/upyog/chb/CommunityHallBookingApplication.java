package org.upyog.chb;


import org.egov.tracer.config.TracerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import({ TracerConfiguration.class })
@SpringBootApplication
@ComponentScan(basePackages = { "org.upyog.chb", "org.upyog.chb.web.controllers" , "org.upyog.chb.config"})
//@EnableFeignClients
public class CommunityHallBookingApplication {

	//TODO: is devtools disable required on prod
    public static void main(String[] args) throws Exception {
    	System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(CommunityHallBookingApplication.class, args);
    }
    
  

}
