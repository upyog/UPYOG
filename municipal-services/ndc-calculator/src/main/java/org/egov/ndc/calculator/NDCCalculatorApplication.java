package org.egov.ndc.calculator;

import org.egov.tracer.config.TracerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@ComponentScan(basePackages = { "org.egov.ndc.calculator", "org.egov.ndc.calculator.web.controllers" , "org.egov.ndc.calculator.config"})
@Import({ TracerConfiguration.class })
public class NDCCalculatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(NDCCalculatorApplication.class, args);
	}

}
