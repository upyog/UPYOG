package org.upyog.sv.config;

<<<<<<< HEAD
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

=======
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
>>>>>>> master-LTS
@Configuration
public class SwaggerConfiguration {

	@Bean
<<<<<<< HEAD
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.servers(List.of(new Server().url("/sv-services")))
				.info(new Info()
						.title("Street Vending Service API")
						.description("API details of the Street Vending Service")
						.version("2.0"));
	}
}
=======
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("org.upyog.sv.web")).paths(PathSelectors.any()).build()
				.apiInfo(apiInfo()); // Completely Optional
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Street Vending API")
				.description("API details of street vending service").version("1.0").build();
	}
}
>>>>>>> master-LTS
