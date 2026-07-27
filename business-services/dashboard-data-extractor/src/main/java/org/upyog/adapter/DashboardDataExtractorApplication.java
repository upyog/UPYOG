package org.upyog.adapter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main Spring Boot application entry point for the Adapter Service microservice.
 * 
 * <p>Consolidates daily metrics extraction, state-configurable multi-module schema mapping
 * via YAML, payload transformation, validation, HTTP ingestion posting to the National Dashboard,
 * and Kafka audit logging.
 */
@SpringBootApplication
@EnableScheduling
@EnableFeignClients(basePackages = { "org.upyog.adapter" })
@ComponentScan(basePackages = { "org.upyog.adapter", "org.egov.tracer" })
public class DashboardDataExtractorApplication {

	/**
	 * Main method to launch the Adapter Service Spring Boot microservice.
	 * 
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(DashboardDataExtractorApplication.class, args);
	}
}
