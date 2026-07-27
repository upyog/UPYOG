package org.upyog.adapter.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.upyog.adapter.common.constants.Module;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuration bean binding multi-module SQL query templates and state-enabled modules.
 *
 * <p>Loads individual query config files (exception.g., {@code pt-schema-mapping.yml}) dynamically
 * at startup according to the modules enabled in {@code application.properties}.
 */
@Slf4j
@Data
@Component
public class SchemaMappingConfig {

    /**
     * List of business modules enabled for extraction in the current state deployment.
     */
    @Value("${extractor.enabled-modules}")
    private List<Module> enabledModules = new ArrayList<>();

    @Autowired
    private ResourceLoader resourceLoader;

    /**
     * Map of dynamic SQL query configurations indexed by module.
     */
    private Map<Module, ModuleQueries> mappings = new EnumMap<>(Module.class);

    /**
     * Post-construct hook to dynamically search and load `<module>-schema-mapping.yml`
     * for every enabled module.
     */
    @PostConstruct
    public void loadSchemaMappings() {
        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
        for (Module module : enabledModules) {
            String fileName = module.name().toLowerCase() + "-schema-mapping.yml";
            String resourcePath = "classpath:" + fileName;
            log.info("SchemaMappingConfig | Loading schema mapping for module {} from {}", module, resourcePath);
            
            Resource resource = resourceLoader.getResource(resourcePath);
            if (!resource.exists()) {
                log.error("SchemaMappingConfig | Schema mapping file {} not found for enabled module {}", fileName, module);
                throw new IllegalStateException("Schema mapping file not found for enabled module: " + module);
            }
            
            try (InputStream is = resource.getInputStream()) {
                ModuleQueries queries = yamlMapper.readValue(is, ModuleQueries.class);
                mappings.put(module, queries);
                log.info("SchemaMappingConfig | Successfully loaded schema mapping for module {}", module);
            } catch (IOException exception) {
                log.error("SchemaMappingConfig | Failed to parse schema mapping file {} for module {}", fileName, module, exception);
                throw new IllegalStateException("Failed to parse schema mapping file: " + fileName, exception);
            }
        }
    }

    /**
     * Gets the query configuration for a specific business module.
     * 
     * @param module the module enum key
     * @return the {@link ModuleQueries} config or {@code null} if not configured
     */
    public ModuleQueries getQueriesForModule(Module module) {
        return mappings.get(module);
    }

    /**
     * Nested configuration class holding SQL query templates for a specific module.
     */
    @Data
    public static class ModuleQueries {
        /**
         * SQL query retrieving scalar metrics and JSON aggregation subqueries in a single database call.
         */
        private String combinedMetricsQuery;

        /**
         * SQL query retrieving daily collection and tax head account detail records.
         */
        private String collectionMetricsQuery;
    }
}
