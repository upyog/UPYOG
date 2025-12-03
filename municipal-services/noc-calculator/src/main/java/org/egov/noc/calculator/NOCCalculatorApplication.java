package org.egov.noc.calculator;

import org.egov.tracer.config.TracerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@ComponentScan(basePackages = { "org.egov.noc.calculator", "org.egov.noc.calculator.web.controllers" , "org.egov.noc.calculator.config"})
@Import({ TracerConfiguration.class })
public class NOCCalculatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(NOCCalculatorApplication.class, args);
	}

}
