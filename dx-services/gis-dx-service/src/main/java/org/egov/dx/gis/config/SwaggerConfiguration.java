package org.egov.dx.gis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * Swagger configuration for GIS DX Service
 */

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GIS DX Service API")
                        .version("1.0")
                        .description("API details of the GIS DX service for municipal services"));
                
    }

  
}
