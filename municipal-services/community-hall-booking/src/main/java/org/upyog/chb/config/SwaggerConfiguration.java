package org.upyog.chb.config;

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
                .servers(List.of(new Server().url("/chb-services")))
                .info(new Info()
                        .title("Community Hall Booking Service API")
                        .description("API details of the Community Hall Booking  Service")
                        .version("2.0"));
    }
}
