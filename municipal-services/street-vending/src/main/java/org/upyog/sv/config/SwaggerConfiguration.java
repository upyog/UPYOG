package org.upyog.sv.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfiguration {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.servers(List.of(new Server().url("/sv-services")))
				.info(new Info()
						.title("Street Vending Service API")
						.description("API details of the Street Vending Service")
						.version("2.0"));
	}
}