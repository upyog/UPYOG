package org.upyog.as.config;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.upyog.as.model.config.PTSchemaMapping;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import jakarta.annotation.PostConstruct;

@Configuration
public class PTSchemaMappingConfig {

    private PTSchemaMapping mapping;

    @PostConstruct
    public void load() throws IOException {
        YAMLMapper mapper = new YAMLMapper();
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("schema-mapping/pt-schema-mapping.yml")) {
            if (is == null) {
                throw new IllegalStateException(
                    "pt-schema-mapping.yml not found on classpath under schema-mapping/");
            }
            this.mapping = mapper.readValue(is, PTSchemaMapping.class);
        }
    }

    @Bean
    public PTSchemaMapping ptSchemaMapping() {
        return mapping;
    }
}