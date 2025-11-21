package org.egov.asset.calculator;

import org.egov.tracer.config.TracerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EntityScan(basePackages = "org.egov.asset.calculator.web.models")
@Import({ TracerConfiguration.class })
public class AssetCalculatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssetCalculatorApplication.class, args);
	}

}
