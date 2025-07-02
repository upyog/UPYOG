package org.upyog.employee.dasboard.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(Arrays.asList(new Server().url("/employee-dashboard")))
                .info(new Info()
                        .title("Employee Dashboard API")
                        .description("API details of the Employee Dashboard service")
                        .version("1.0"));
    }
}