package org.egov.gis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * Swagger configuration for GIS Service
 */
@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI gisServiceOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("GIS Service API")
                .description("API details of the GIS service for municipal services")
                .version("1.0"));
    }
}
