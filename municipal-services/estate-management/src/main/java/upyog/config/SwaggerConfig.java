package upyog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI estateManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Estate Management Service")
                        .description("APIs for Estate asset registration and allotment in UPYOG")
                        .version("1.0.0")
                        .contact(new Contact().name("UPYOG Team").email("keshav@niua.org")));
    }
}
