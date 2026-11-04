package org.upyog.draft.config;

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
                .info(new Info()
                        .title("UPYOG Draft Service API")
                        .description("Centralized draft storage for municipal service applications (TL, SV, ADV, PT)")
                        .version("1.0.0"))
                .servers(List.of(
                        new Server().url("/upyog-draft-service").description("Default Server")
                ));
    }
}
