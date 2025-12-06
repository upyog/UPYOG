package org.upyog.rs.config;

<<<<<<< HEAD
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
				.servers(List.of(new Server().url("/request-service")))
				.info(new Info()
						.title("Request Service API")
						.description("API details of  Request Service API")
						.version("2.0"));
	}
}
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
@Configuration
public class SwaggerConfiguration {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("org.upyog.rs.web.controllers")) 
                .paths(PathSelectors.any()).build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Request Service API") 
                .description("API details of the Request service").version("1.0").build();
    }
}
>>>>>>> master-LTS
