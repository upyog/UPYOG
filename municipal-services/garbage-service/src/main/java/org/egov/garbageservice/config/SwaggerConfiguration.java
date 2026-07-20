package org.egov.garbageservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

/**
 * Configuration for  Swagger documentation.
 * Exposes an OpenAPI bean that provides metadata (title, description, version)
 * and the default server URL for this service. Springdoc will pick up this bean
 * and render Swagger UI / OpenAPI docs accordingly.
 */
@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Garbage Service API")
                        .description("API details of Garbage Collection and Payment service")
                        .version("1.0"))
                .servers(List.of(
                        new Server().url("/garbage-service").description("Default Server")
                ));
    }
}
