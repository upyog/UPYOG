package org.egov.filemgmnt.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Profile({ "local" })
@Slf4j
class OpenApiDocConfiguration {

    static {
        System.setProperty("springdoc.api-docs.path", "/api-docs");
        System.setProperty("springdoc.swagger-ui.path", "/swagger");
        System.setProperty("springdoc.swagger-ui.supportedSubmitMethods", "");
        log.info("Overriding springdoc properties...");
    }

    @Bean
    GroupedOpenApi fileMgmntApi() {
        return GroupedOpenApi.builder()
                             .group("v1.0")
                             .pathsToMatch("/filemgmnt/v1/**")
                             .packagesToScan("org.egov.filemgmnt")
                             .build();
    }

    @Bean
    OpenAPI fileMgmntApiInfo() {
        return new OpenAPI()//
                            .info(new Info()//
                                            .title("File Management API's")
                                            .description("egov File Management Service API's")
                                            .version("v1.0"))
                            .addServersItem(new Server()//
                                                        .url("http://localhost:8080/filemgmnt")
                                                        .description("Local Development Server"));
    }
}
