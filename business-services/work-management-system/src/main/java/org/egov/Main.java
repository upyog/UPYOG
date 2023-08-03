package org.egov;

import org.egov.tracer.config.TracerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Import({TracerConfiguration.class})
@SpringBootApplication

@ComponentScan(basePackages = {"org.egov", "org.egov.web.controllers", "org.egov.config"})
public class Main {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

}
