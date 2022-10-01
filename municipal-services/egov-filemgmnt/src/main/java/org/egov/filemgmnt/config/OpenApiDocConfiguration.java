package org.egov.filemgmnt.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
class OpenApiDocConfiguration {

    @Bean
    GroupedOpenApi fileMgmntApi() {
        return GroupedOpenApi.builder()
                             .group("1.0.0")
                             .pathsToMatch("/v1/**")
                             .packagesToScan("org.egov.filemgmnt.web")
                             .build();
    }

    @Bean
    OpenAPI fileMgmntApiInfo() {
        return new OpenAPI().info(new Info().title("File Management API's")
                                            .description("egov File Management Service API's")
                                            .version("1.0.0"))
                            .addServersItem(new Server().url("http://localhost:8080/filemgmnt")
                                                        .description("Local Development Server"));
        // .schema("Error", getErrorSchema());
    }

//    private Schema<?> getErrorSchema() {
//        return new Schema<>().type("object")
//                             .description("Error details")
//                             .addProperty("code",
//                                          new Schema<>().type("string")
//                                                        .description("error message code")
//                                                        .example("NOT_FOUND"));
//    }
}

// https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations#OpenAPIDefinition
