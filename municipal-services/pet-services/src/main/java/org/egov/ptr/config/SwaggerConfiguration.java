package org.egov.ptr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pet Registration API")
                        .description("API details of Pet Registration service")
                        .version("1.0"))
                .servers(List.of(
                        new Server().url("/pet-services").description("Default Server")
                ));
    }
}
