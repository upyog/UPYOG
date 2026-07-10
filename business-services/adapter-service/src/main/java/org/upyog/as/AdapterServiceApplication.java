package org.upyog.as;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the adapter service Spring Boot application.
 */
@SpringBootApplication
public class AdapterServiceApplication {

	/**
	 * Starts the adapter service.
	 *
	 * @param args startup arguments passed to Spring Boot
	 */
	public static void main(String[] args) {
		SpringApplication.run(AdapterServiceApplication.class, args);
	}

}
